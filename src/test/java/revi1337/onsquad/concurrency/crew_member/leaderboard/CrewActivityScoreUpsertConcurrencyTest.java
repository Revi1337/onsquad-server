package revi1337.onsquad.concurrency.crew_member.leaderboard;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@Sql({"/mysql-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayName("CrewActivityScore UPSERT 동시성: 같은 (crew_id, member_id) 행에 대한 동시 UPSERT의 정합성 검증")
class CrewActivityScoreUpsertConcurrencyTest {

    private static final int THREAD_COUNT = 10;
    private static final int WEIGHT = 10;

    @MockBean
    private NotificationEventListener notificationEventListener;

    @MockBean
    private FileRecycleBinRepository fileRecycleBinRepository;

    @MockBean
    private ThrottlingAspect throttlingAspect;

    @MockBean
    private RedisCacheAspect redisCacheAspect;

    @Autowired
    private CrewActivityScoreRepository crewActivityScoreRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("""
            [비결정적] 동일한 (crew_id, member_id) 조합에 대해 여러 스레드가 동시에 UPSERT 해도
            --> 예외/중복 행 없이 weight 가 (THREAD_COUNT * WEIGHT) 로 정확히 누적되어야 함 (lost update 없음)
            """)
    void concurrencyUpsertScore() {
        // given
        Long crewId = 1L;
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        // when
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                try {
                    crewActivityScoreRepository.upsertScore(crewId, memberId, WEIGHT, now);
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }, executor));
        }
        startLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // then
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT weight FROM crew_activity_score WHERE crew_id = ? AND member_id = ?",
                crewId, memberId
        );
        assertSoftly(softly -> {
            softly.assertThat(exceptions)
                    .as("동시 UPSERT 도중 예외/데드락이 발생하지 않아야 한다.")
                    .isEmpty();
            softly.assertThat(rows)
                    .as("유니크 제약(crew_id, member_id)에 의해 행이 정확히 1개여야 한다. (중복 행 없음)")
                    .hasSize(1);
            softly.assertThat(rows.get(0).get("weight"))
                    .as("모든 스레드의 weight 가 누락 없이 합산되어야 한다.")
                    .isEqualTo(THREAD_COUNT * WEIGHT);
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
