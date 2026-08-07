package revi1337.onsquad.concurrency.crew_member.leaderboard;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.Optional;
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
import revi1337.onsquad.crew_member.domain.entity.CrewActivityScore;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

/**
 * {@code CrewActivityScoreSnapshotConsistencyTest}가 단일 스레드로 결정론적으로 재현하는
 * "배치가 {@code fetchAggregatedRankedMembers}로 랭킹을 읽은 시점과, {@code deleteByLastActivityAtBetween}으로
 * 정리를 실행하는 시점 사이에 새로운 UPSERT 가 끼어드는 레이스"를, 순차 호출이 아니라 진짜 스레드 두 개
 * ({@code ExecutorService} + {@code CountDownLatch} 체크포인트)로 재현한다.
 * <p>
 * {@code deleteByLastActivityAtBetween(from, to)}는 행의 "현재 weight 값"을 보지 않고
 * "시간 범위"만으로 삭제하므로, 실제 동시 실행 환경에서도 이 레이스가 재현된다. 아래 assertion은
 * "실제로 일어나는 동작"을 그대로 기대값으로 명시하므로 이 테스트는 통과(GREEN)한다 —
 * 이건 버그가 없다는 뜻이 아니라, 이게 알려진/문서화된 버그라는 뜻이다.
 */
@Sql({"/mysql-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayName("CrewActivityScore 랭킹조회-삭제 레이스(4-1): 시간 범위 통째 삭제가 실시간 UPSERT와 부딪히는 경합의 정합성 검증")
class CrewActivityScoreSnapshotSubtractRaceConcurrencyTest {

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
    private CrewActivityScoreRepository crewActivityScoreRepository;

    @Autowired
    private CrewActivityScoreJpaRepository crewActivityScoreJpaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * {@code deleteByLastActivityAtBetween}은 시간 범위만 보고 행을 통째로 지우기 때문에,
     * 실제 동시 실행에서도 새로 들어온 활동이 유실되는 게 "실제 동작"이다. 아래 assertion은 그 실제
     * 동작을 그대로 기대값으로 삼으므로 통과(GREEN)한다 — 알려진 버그를 문서화하는 것.
     */
    @Test
    @DisplayName("[알려진 버그/동시성] 랭킹 조회 직후 별도 스레드가 같은 (crew_id, member_id)에 UPSERT를 커밋해도, deleteByLastActivityAtBetween은 행을 통째로 지워 새 활동을 유실시킨다")
    void deleteByLastActivityAtBetween_losesNewlyUpsertedWeight_whenUpsertHappensConcurrentlyViaRealThreads() {
        // given: 특정 (crew_id, member_id) 행을 weight=5 로 만들어둔다 (activityTime 은 [from, to] 범위 안)
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        LocalDateTime from = activityTime.minusDays(1);
        LocalDateTime to = activityTime.plusDays(1);
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 5, activityTime);

        // when: Thread A(먼저 실행)는 랭킹을 읽고 신호를 보낸 뒤, Thread B의 UPSERT 커밋을 기다렸다가 정리(삭제)를 수행한다.
        //       Thread B는 Thread A의 랭킹 조회가 끝난 뒤에야 같은 행에 +10 UPSERT를 적용한다(weight 5 -> 15).
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch rankingFetched = new CountDownLatch(1);
        CountDownLatch upsertCommitted = new CountDownLatch(1);

        CompletableFuture<Void> threadA = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            crewActivityScoreRepository.fetchAggregatedRankedMembers(from, to, 10);
            rankingFetched.countDown();

            // upsertCommitted.countDown()은 threadB의 upsertScore(raw JDBC, autocommit) 호출이 "리턴한 뒤"
            // 실행되므로, 이 시점엔 이미 UPSERT가 DB에 커밋 완료된 상태다 — CountDownLatch의
            // happens-before 보장만으로 충분히 결정론적이라 별도 sleep으로 여유를 줄 필요가 없다.
            waitToStart(upsertCommitted);
            // deleteByLastActivityAtBetween은 (save()와 달리) 자체 @Transactional이 없다 —
            // 4-1 시점 프로덕션 코드는 이 메서드가 refreshLeaderboards()의 @Transactional 안에서만
            // 호출된다는 전제였으므로, 순수 스레드 단독 호출을 위해 테스트에서 트랜잭션을 명시적으로 열어준다.
            new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                    crewActivityScoreRepository.deleteByLastActivityAtBetween(from, to));
        }, executor);

        CompletableFuture<Void> threadB = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            waitToStart(rankingFetched);
            crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 10, activityTime.plusMinutes(1));
            upsertCommitted.countDown();
        }, executor);

        startLatch.countDown();
        CompletableFuture.allOf(threadA, threadB).join();
        executor.shutdown();

        // then: 4-1 시점엔 새로 들어온 10점까지 deleteByLastActivityAtBetween에 의해 행째로 유실되는 게 "실제 동작"이다 (알려진 버그를 그대로 기록, 실제 동시 실행 환경에서도 재현됨)
        Optional<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll().stream()
                .filter(row -> row.getCrewId().equals(crew.getId()) && row.getMemberId().equals(member.getId()))
                .findFirst();

        assertSoftly(softly -> softly.assertThat(found)
                .as("그 사이 새로 들어온 활동(weight 10)까지 deleteByLastActivityAtBetween에 의해 행째로 유실된다 (알려진 버그)")
                .isEmpty());
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
