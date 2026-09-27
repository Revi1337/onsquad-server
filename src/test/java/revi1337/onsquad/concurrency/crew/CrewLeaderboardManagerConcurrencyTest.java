package revi1337.onsquad.concurrency.crew;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
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
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew_member.application.leaderboard.CompositeScore;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardKeyMapper;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@ContextConfiguration(initializers = RedisTestContainerInitializer.class, classes = {CrewLeaderboardManager.class})
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
    @DisplayName("비원자적 조회->계산->저장 3단계 상황에서의 Lost Update 상황을 검증한다.")
    void lostUpdateVerificationTest() {
        Long crewId = 1L;
        Long memberId = 2L;
        String namedSortedSet = CrewLeaderboardKeyMapper.toLeaderboardKey(crewId);
        String specificName = CrewLeaderboardKeyMapper.toMemberKey(memberId);
        Instant applyAt = Instant.now();
        CrewActivity baseActivity = CrewActivity.SQUAD_COMMENT; // SCORE: 1
        CrewActivity activity1 = CrewActivity.CREW_PARTICIPANT; // SCORE: 5
        CrewActivity activity2 = CrewActivity.SQUAD_CREATE; // SCORE: 10
        crewLeaderboardManager.applyActivity(crewId, memberId, applyAt, baseActivity);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch readLatch = new CountDownLatch(2);

        CompletableFuture<Void> staleThreadFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            Double currentScore = stringRedisTemplate.opsForZSet().score(namedSortedSet, specificName);
            readLatch.countDown();
            waitToStart(readLatch);

            long score = currentScore == null ? 0L : CompositeScore.from(currentScore).getActualScore();
            long nextScore = score + activity1.getScore();
            CompositeScore staleNextCompositeScore = CompositeScore.of(nextScore, applyAt);
            stringRedisTemplate.opsForZSet().add(namedSortedSet, specificName, staleNextCompositeScore.toRedisScore());
        }, executor);

        CompletableFuture<Void> concurrentThreadFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            Double currentScore = stringRedisTemplate.opsForZSet().score(namedSortedSet, specificName);
            readLatch.countDown();
            waitToStart(readLatch);

            long score = currentScore == null ? 0L : CompositeScore.from(currentScore).getActualScore();
            long nextScore = score + activity2.getScore();
            CompositeScore staleNextCompositeScore = CompositeScore.of(nextScore, applyAt);
            stringRedisTemplate.opsForZSet().add(namedSortedSet, specificName, staleNextCompositeScore.toRedisScore());
        }, executor);

        startLatch.countDown();
        CompletableFuture.allOf(staleThreadFuture, concurrentThreadFuture).join();
        executor.shutdown();

        long expectScore = baseActivity.getScore() + activity1.getScore() + activity2.getScore(); // 16
        long actualScore = crewLeaderboardManager.getScore(crewId, memberId);

        assertThat(actualScore)
                .as(String.format(
                        "원래라면 %s점이어야하는데, Lost Update 로 인해 %s점 또는 %s점이 나오게 된다.",
                        expectScore, baseActivity.getScore() + activity1.getScore(), baseActivity.getScore() + activity2.getScore()
                ))
                .isNotEqualTo(expectScore);
    }

    private void waitToStart(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
