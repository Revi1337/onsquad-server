package revi1337.onsquad.concurrency.crew_member.leaderboard;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityLog;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityLogJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityLogRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

/**
 * 4번(단일 활동 로그 테이블, append-only) 시점의 리더보드 배치 정리 로직은
 * {@link CrewActivityLogRepository#deleteByCreatedAtBetween(LocalDateTime, LocalDateTime)}로
 * {@code crew_activity_log} 행을 "시간 범위"만 보고 통째로 삭제한다.
 * <p>
 * 4-1({@code CrewActivityScoreSnapshotSubtractRaceConcurrencyTest})의 레이스는 "기존 행의 UPSERT
 * 증분이 삭제로 함께 날아가는" 형태였지만, 4번은 append-only라 행이 갱신되지 않는다 — 대신 배치가
 * {@code fetchAggregatedRankedMembers}로 집계를 읽은 시점과 {@code deleteByCreatedAtBetween}으로
 * 정리를 실행하는 시점 사이에 실시간 이벤트로 새 활동 행이 insert되면, 그 행은 한 번도 집계되지
 * 못한 채로 시간 범위에 걸렸다는 이유만으로 통째로 삭제된다. InnoDB의 DELETE는 스냅샷이 아니라
 * 실행 시점의 최신 커밋 데이터를 기준으로 매칭 행을 찾는 "current read"이기 때문에, 이 레이스는
 * 실제 동시 실행 환경에서도 재현된다.
 * <p>
 * 아래 assertion은 "실제로 일어나는 동작"을 그대로 기대값으로 명시하므로 이 테스트는 통과(GREEN)한다
 * — 이건 버그가 없다는 뜻이 아니라, 이게 알려진/문서화된 버그라는 뜻이다.
 */
@Sql({"/mysql-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayName("CrewActivityLog 랭킹조회-삭제 레이스(4번): 시간 범위 통째 삭제가 실시간 INSERT와 부딪히는 경합의 정합성 검증")
class CrewActivityLogSnapshotDeleteRaceConcurrencyTest {

    @MockBean
    private NotificationEventListener notificationEventListener;

    @MockBean
    private FileRecycleBinRepository fileRecycleBinRepository;

    @MockBean
    private ThrottlingAspect throttlingAspect;

    @MockBean
    private RedisCacheAspect redisCacheAspect;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewActivityLogRepository crewActivityLogRepository;

    @Autowired
    private CrewActivityLogJpaRepository crewActivityLogJpaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * {@code deleteByCreatedAtBetween}은 시간 범위만 보고 행을 통째로 지우기 때문에, 실제 동시
     * 실행에서도 조회 이후 들어온 새 활동이 미집계 상태로 유실되는 게 "실제 동작"이다. 아래
     * assertion은 그 실제 동작을 그대로 기대값으로 삼으므로 통과(GREEN)한다 — 알려진 버그를
     * 문서화하는 것.
     */
    @Test
    @DisplayName("[알려진 버그/동시성] 로그 조회 직후 별도 스레드가 새 활동을 커밋해도, deleteByCreatedAtBetween은 그 활동까지 함께 지워 미집계 상태로 유실시킨다")
    void deleteByCreatedAtBetween_losesNewlyInsertedActivity_whenInsertHappensConcurrentlyViaRealThreads() {
        // given: 기존 활동 로그 하나 저장 (createdAt 은 [from, to] 범위 안, weight=5)
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        LocalDateTime from = activityTime.minusDays(1);
        LocalDateTime to = activityTime.plusDays(1);
        crewActivityLogRepository.save(new CrewActivityLog(
                crew.getId(), member.getId(), CrewActivity.CREW_PARTICIPANT, 5, activityTime
        ));

        // when: Thread A(먼저 실행)는 집계를 읽고 신호를 보낸 뒤, Thread B의 새 활동 커밋을 기다렸다가
        //       정리(삭제)를 수행한다. Thread B는 Thread A의 집계 조회가 끝난 뒤에야 같은 크루/멤버에
        //       새 활동 로그를 insert한다(별도 행, activityType=SQUAD_COMMENT, weight=10).
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch logFetched = new CountDownLatch(1);
        CountDownLatch newActivityCommitted = new CountDownLatch(1);
        List<CrewRankerCandidate>[] fetchedHolder = new List[1];

        CompletableFuture<Void> threadA = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            fetchedHolder[0] = crewActivityLogRepository.fetchAggregatedRankedMembers(from, to, 10);
            logFetched.countDown();

            // newActivityCommitted.countDown()은 threadB의 save() 호출이 "리턴한 뒤" 실행되므로,
            // 이 시점엔 이미 새 활동이 DB에 커밋 완료된 상태다 — CountDownLatch의 happens-before
            // 보장만으로 충분히 결정론적이라 별도 sleep으로 여유를 줄 필요가 없다.
            waitToStart(newActivityCommitted);
            // deleteByCreatedAtBetween은 (save()와 달리) 자체 @Transactional이 없다 — 4번 시점
            // 프로덕션 코드는 이 메서드가 refreshLeaderboards()의 @Transactional 안에서만 호출된다는
            // 전제였으므로, 순수 스레드 단독 호출을 위해 테스트에서 트랜잭션을 명시적으로 열어준다.
            new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                    crewActivityLogRepository.deleteByCreatedAtBetween(from, to));
        }, executor);

        CompletableFuture<Void> threadB = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            waitToStart(logFetched);
            crewActivityLogRepository.save(new CrewActivityLog(
                    crew.getId(), member.getId(), CrewActivity.SQUAD_COMMENT, 10, activityTime.plusMinutes(1)
            ));
            newActivityCommitted.countDown();
        }, executor);

        startLatch.countDown();
        CompletableFuture.allOf(threadA, threadB).join();
        executor.shutdown();

        // then: Thread A의 집계는 Thread B가 아직 커밋하지 않은 시점에 읽었으므로 weight=5만 반영하고
        //       (Thread B의 10점은 한 번도 집계되지 않음), 그런데도 deleteByCreatedAtBetween은 Thread B가
        //       새로 커밋한 행까지 시간 범위에 걸렸다는 이유만으로 함께 지워버린다 (알려진 버그).
        List<CrewActivityLog> remaining = crewActivityLogJpaRepository.findAll();

        assertSoftly(softly -> {
            softly.assertThat(fetchedHolder[0])
                    .as("Thread A의 집계 조회 시점엔 Thread B의 새 활동이 아직 커밋되지 않아 weight=5만 반영된다")
                    .hasSize(1)
                    .first()
                    .extracting(CrewRankerCandidate::score)
                    .isEqualTo(5L);
            softly.assertThat(remaining)
                    .as("한 번도 집계되지 못한 Thread B의 새 활동(weight 10)까지 deleteByCreatedAtBetween에 의해 통째로 유실된다 (알려진 버그)")
                    .isEmpty();
        });
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
