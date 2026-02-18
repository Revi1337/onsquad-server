package revi1337.onsquad.concurrency.crew;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.container.RedisTestContainerSupport;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@ContextConfiguration(initializers = RedisTestContainerSupport.RedisInitializer.class, classes = {CrewLeaderboardManager.class})
@ImportAutoConfiguration({RedisAutoConfiguration.class, JacksonAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
class CrewLeaderboardManagerConcurrencyTest {

    @Autowired
    private CrewLeaderboardManager crewLeaderboardManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("멀티 스레드 경합 상황에서 원자적 스코어 업데이트를 수행하여 Lost Update 발생을 차단한다")
    void success2() throws InterruptedException {
        Long crewId = 1L;
        Long memberId = 2L;
        Instant applyAt = Instant.now();
        CrewActivity crewActivity = CrewActivity.SQUAD_CREATE; // SCORE: 10
        int threadCount = 30;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    crewLeaderboardManager.applyActivity(crewId, memberId, applyAt, crewActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        long expectedScore = (long) threadCount * crewActivity.getScore();
        long actualScore = crewLeaderboardManager.getScore(crewId, memberId);
        assertThat(actualScore).isEqualTo(expectedScore);
    }
}
