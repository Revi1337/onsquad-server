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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityScore;
import revi1337.onsquad.crew_member.domain.model.CrewActivityScoreSnapshot;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

/**
 * {@code CrewActivityScoreSnapshotConsistencyTest} 가 단일 스레드로 결정론적으로 재현하는
 * "스냅샷을 읽은 시점과 subtractCountedWeight/deleteZeroWeightRows 를 실행하는 시점 사이에
 * 새로운 UPSERT 가 끼어드는 레이스" 시나리오를, 순차 호출이 아니라 진짜 스레드 두 개
 * ({@code ExecutorService} + {@code CountDownLatch} 체크포인트)로 재현한다.
 * <p>
 * H2 기반 {@code PersistenceLayerTestSupport}(클래스 레벨 자동 롤백 트랜잭션)에서는 워커 스레드가
 * given 데이터를 보지 못하거나 락 경합이 발생하므로, 이 시나리오는 별도로 TestContainers MySQL 기반의
 * 독립 클래스({@code @SpringBootTest})로 분리하여 실제 커밋이 이루어지는 환경에서 검증한다.
 */
@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@Sql({"/mysql-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayName("CrewActivityScore 스냅샷-차감 레이스: 스냅샷을 읽은 직후 같은 행에 새 UPSERT가 커밋되는 경합의 정합성 검증")
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

    @Test
    @DisplayName("[동시성] 스냅샷을 읽은 직후 별도 스레드가 같은 (crew_id, member_id)에 UPSERT를 커밋해도, 배치는 스냅샷에 담긴 만큼만 정확히 차감하여 새로 들어온 활동을 유실시키지 않는다")
    void subtractCountedWeight_preservesNewlyUpsertedWeight_whenUpsertHappensConcurrentlyViaRealThreads() {
        // given: 특정 (crew_id, member_id) 행을 weight=5 로 만들어둔다
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 5, activityTime);

        // when: Thread A(먼저 실행)는 스냅샷을 읽고 신호를 보낸 뒤, Thread B의 UPSERT 커밋을 기다렸다가 차감/정리를 수행한다.
        //       Thread B는 Thread A의 스냅샷 읽기가 끝난 뒤에야 같은 행에 +10 UPSERT를 적용한다(weight 5 -> 15).
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch snapshotFetched = new CountDownLatch(1);
        CountDownLatch upsertCommitted = new CountDownLatch(1);

        CompletableFuture<Void> threadA = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            List<CrewActivityScoreSnapshot> snapshot = crewActivityScoreRepository
                    .fetchSnapshot(activityTime.minusDays(1), activityTime.plusDays(1));
            snapshotFetched.countDown();

            // upsertCommitted.countDown()은 threadB의 upsertScore(raw JDBC, autocommit) 호출이 "리턴한 뒤"
            // 실행되므로, 이 시점엔 이미 UPSERT가 DB에 커밋 완료된 상태다 — CountDownLatch의
            // happens-before 보장만으로 충분히 결정론적이라 별도 sleep으로 여유를 줄 필요가 없다.
            waitToStart(upsertCommitted);
            crewActivityScoreRepository.subtractCountedWeight(snapshot);
            crewActivityScoreRepository.deleteZeroWeightRows();
        }, executor);

        CompletableFuture<Void> threadB = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            waitToStart(snapshotFetched);
            crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 10, activityTime.plusMinutes(1));
            upsertCommitted.countDown();
        }, executor);

        startLatch.countDown();
        CompletableFuture.allOf(threadA, threadB).join();
        executor.shutdown();

        // then: 행이 삭제되지 않고 남아있어야 하며, weight 는 정확히 10(15-5) 이어야 한다
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found)
                    .as("새로 들어온 활동은 deleteZeroWeightRows 에 의해 삭제되면 안 된다")
                    .hasSize(1);
            softly.assertThat(found.get(0).getWeight())
                    .as("기존 5점만 정확히 차감되고, 그 사이 들어온 10점은 유실 없이 보존되어야 한다 (15-5=10)")
                    .isEqualTo(10);
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
