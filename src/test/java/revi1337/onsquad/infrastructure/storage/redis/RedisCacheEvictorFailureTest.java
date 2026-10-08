package revi1337.onsquad.infrastructure.storage.redis;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.container.UnreachableRedis;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils.ScanSize;

/**
 * 공용 유틸은 Redis 장애를 삼키지 않고 호출부(어댑터/초기화 코드)에 그대로 전파해야 한다.
 */
class RedisCacheEvictorFailureTest {

    private final StringRedisTemplate brokenRedisTemplate = UnreachableRedis.stringRedisTemplate();

    @Test
    @DisplayName("scanKeysAndUnlink(pattern) 는 Redis 장애 시 예외를 던진다")
    void scanKeysAndUnlinkByPattern_throws() {
        assertThatThrownBy(() -> RedisCacheEvictor.scanKeysAndUnlink(brokenRedisTemplate, "crew:*"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("scanKeysAndUnlink(pattern, scanSize) 는 Redis 장애 시 예외를 던진다")
    void scanKeysAndUnlinkByPatternAndScanSize_throws() {
        assertThatThrownBy(() -> RedisCacheEvictor.scanKeysAndUnlink(brokenRedisTemplate, "crew:*", ScanSize.LIGHT))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("scanKeysAndUnlink(patterns) 는 Redis 장애 시 예외를 던진다")
    void scanKeysAndUnlinkByPatterns_throws() {
        assertThatThrownBy(() -> RedisCacheEvictor.scanKeysAndUnlink(brokenRedisTemplate, List.of("crew:*", "squad:*")))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("unlinkKey 는 Redis 장애 시 예외를 던진다")
    void unlinkKey_throws() {
        assertThatThrownBy(() -> RedisCacheEvictor.unlinkKey(brokenRedisTemplate, "crew:1"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("unlinkKeys 는 Redis 장애 시 예외를 던진다")
    void unlinkKeys_throws() {
        assertThatThrownBy(() -> RedisCacheEvictor.unlinkKeys(brokenRedisTemplate, List.of("crew:1", "crew:2")))
                .isInstanceOf(DataAccessException.class);
    }
}
