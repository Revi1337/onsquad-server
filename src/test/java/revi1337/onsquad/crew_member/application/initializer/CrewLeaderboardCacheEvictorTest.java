package revi1337.onsquad.crew_member.application.initializer;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardKeyMapper;

@ActiveProfiles("local")
@Import(CrewLeaderboardCacheEvictor.class)
@ImportAutoConfiguration(RedisAutoConfiguration.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
@ExtendWith(SpringExtension.class)
class CrewLeaderboardCacheEvictorTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CrewLeaderboardCacheEvictor leaderboardCacheEvictor;

    @Test
    @DisplayName("애플리케이션 구동 완료 시 리더보드 관련 모든 캐시를 삭제한다")
    void onApplicationEvent() {
        String leaderboardKey = CrewLeaderboardKeyMapper.toLeaderboardKey(1L);
        String leaderboardSnapshotKey = CrewLeaderboardKeyMapper.toLeaderboardSnapshotKey(2L);
        stringRedisTemplate.opsForValue().set(leaderboardKey, "value");
        stringRedisTemplate.opsForValue().set(leaderboardSnapshotKey, "value");

        leaderboardCacheEvictor.onApplicationEvent();

        assertSoftly(softly -> {
            softly.assertThat(stringRedisTemplate.hasKey(leaderboardKey)).isFalse();
            softly.assertThat(stringRedisTemplate.hasKey(leaderboardSnapshotKey)).isFalse();
        });
    }
}
