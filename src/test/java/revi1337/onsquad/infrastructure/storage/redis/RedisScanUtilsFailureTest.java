package revi1337.onsquad.infrastructure.storage.redis;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.container.UnreachableRedis;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils.ScanSize;

class RedisScanUtilsFailureTest {
    @Test
    @DisplayName("scanKeys(pattern) 는 Redis 연결 불가 시 예외를 던진다")
    void scanKeysByPattern_throws_whenRedisIsUnreachable() {
        StringRedisTemplate brokenRedisTemplate = UnreachableRedis.stringRedisTemplate();

        assertThatThrownBy(() -> RedisScanUtils.scanKeys(brokenRedisTemplate, "crew:*"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("scanKeys(pattern, scanSize) 는 Redis 연결 불가 시 예외를 던진다")
    void scanKeysByPatternAndScanSize_throws_whenRedisIsUnreachable() {
        StringRedisTemplate brokenRedisTemplate = UnreachableRedis.stringRedisTemplate();

        assertThatThrownBy(() -> RedisScanUtils.scanKeys(brokenRedisTemplate, "crew:*", ScanSize.BULK))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("scanKeys(patterns) 는 Redis 연결 불가 시 예외를 던진다")
    void scanKeysByPatterns_throws_whenRedisIsUnreachable() {
        StringRedisTemplate brokenRedisTemplate = UnreachableRedis.stringRedisTemplate();

        assertThatThrownBy(() -> RedisScanUtils.scanKeys(brokenRedisTemplate, List.of("crew:*", "squad:*")))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("SCAN 명령 자체가 실패하면 예외를 삼키지 않고 던진다")
    void scanKeys_throws_whenScanCommandFails() {
        RedisKeyCommands keyCommands = mock(RedisKeyCommands.class);
        when(keyCommands.scan(any(ScanOptions.class))).thenThrow(new RedisSystemException("scan failed", new RuntimeException()));

        assertThatThrownBy(() -> RedisScanUtils.scanKeys(templateExecutingWith(keyCommands), "crew:*"))
                .isInstanceOf(RedisSystemException.class)
                .hasMessageContaining("scan failed");
    }

    @Test
    @DisplayName("커서 순회 도중 실패하면 예외를 던지고 커서는 닫는다")
    @SuppressWarnings("unchecked")
    void scanKeys_throwsAndClosesCursor_whenIterationFails() {
        Cursor<byte[]> cursor = mock(Cursor.class);
        when(cursor.hasNext()).thenReturn(true);
        when(cursor.next()).thenThrow(new RedisSystemException("cursor broken", new RuntimeException()));
        RedisKeyCommands keyCommands = mock(RedisKeyCommands.class);
        when(keyCommands.scan(any(ScanOptions.class))).thenReturn(cursor);

        assertThatThrownBy(() -> RedisScanUtils.scanKeys(templateExecutingWith(keyCommands), "crew:*"))
                .isInstanceOf(RedisSystemException.class)
                .hasMessageContaining("cursor broken");
        verify(cursor).close();
    }

    @SuppressWarnings("unchecked")
    private StringRedisTemplate templateExecutingWith(RedisKeyCommands keyCommands) {
        RedisConnection connection = mock(RedisConnection.class);
        when(connection.keyCommands()).thenReturn(keyCommands);
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        when(template.execute(any(RedisCallback.class))).thenAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(connection);
        });
        return template;
    }
}
