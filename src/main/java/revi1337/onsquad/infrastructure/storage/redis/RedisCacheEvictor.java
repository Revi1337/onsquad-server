package revi1337.onsquad.infrastructure.storage.redis;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils.ScanSize;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class RedisCacheEvictor {

    /**
     * Removes all keys from all databases on the Redis instance using the {@code FLUSHALL} command.
     * <p>
     * <b>Warning:</b> This operation is destructive and affects every database (0-15) in the instance.
     * It removes all data, including sessions, distributed locks, and rate-limit counters. Use only in local or integration testing environments.
     *
     * @param stringRedisTemplate the Redis template used to obtain the connection
     */
    public static void flushAll(StringRedisTemplate stringRedisTemplate) {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    /**
     * Scans for keys matching the given pattern and deletes them asynchronously using the {@code UNLINK} command with the {@link ScanSize#DEFAULT} strategy.
     *
     * @param stringRedisTemplate the Redis template for execution
     * @param pattern             the key pattern to match (e.g., "squad:*")
     */
    public static void scanKeysAndUnlink(StringRedisTemplate stringRedisTemplate, String pattern) {
        scanKeysAndUnlink(stringRedisTemplate, pattern, RedisScanUtils.ScanSize.DEFAULT);
    }

    /**
     * Scans for keys matching the given pattern and deletes them asynchronously using the {@code UNLINK} command with a specified {@link ScanSize} strategy.
     * <p>
     * This method utilizes a cursor-based scan to identify keys safely without blocking the Redis event loop.
     *
     * @param stringRedisTemplate the Redis template for execution
     * @param pattern             the key pattern to match
     * @param scanSize            the strategy for scan iteration depth
     */
    public static void scanKeysAndUnlink(StringRedisTemplate stringRedisTemplate, String pattern, ScanSize scanSize) {
        if (ObjectUtils.isEmpty(pattern)) {
            return;
        }
        try {
            List<String> keys = RedisScanUtils.scanKeys(stringRedisTemplate, pattern, scanSize);
            unlinkKeys(stringRedisTemplate, keys);
        } catch (RuntimeException e) {
            log.error("{}: cannot destroy redis-cache for pattern {}", RedisHealthLoggingIndicator.REDIS_HEALTH_CHECK_ERROR_LOG, pattern);
        }
    }

    /**
     * Scans for keys matching multiple patterns and performs a bulk non-blocking deletion.
     * <p>
     * Identifies unique keys matching any of the provided patterns and reclaims memory in a background thread.
     *
     * @param stringRedisTemplate the Redis template for execution
     * @param patterns            the list of patterns to scan (e.g., ["crew:*", "announcement:*"])
     */
    public static void scanKeysAndUnlink(StringRedisTemplate stringRedisTemplate, List<String> patterns) {
        if (CollectionUtils.isEmpty(patterns)) {
            return;
        }
        try {
            List<String> keys = RedisScanUtils.scanKeys(stringRedisTemplate, patterns);
            unlinkKeys(stringRedisTemplate, keys);
        } catch (RuntimeException e) {
            log.error("{}: cannot destroy redis-cache for patterns {}", RedisHealthLoggingIndicator.REDIS_HEALTH_CHECK_ERROR_LOG, patterns);
        }
    }

    /**
     * Performs a non-blocking deletion of a single specific key.
     *
     * @param stringRedisTemplate the Redis template for execution
     * @param key                 the exact key to remove
     */
    public static void unlinkKey(StringRedisTemplate stringRedisTemplate, String key) {
        if (ObjectUtils.isEmpty(key)) {
            return;
        }
        unlinkKeys(stringRedisTemplate, List.of(key));
    }

    /**
     * Performs a non-blocking bulk deletion for a list of specific keys.
     * <p>
     * Reclaims memory asynchronously. This is significantly safer than {@code DEL} for large objects as it prevents blocking the Redis main thread.
     *
     * @param stringRedisTemplate the Redis template for execution
     * @param keys                the list of specific keys to remove
     */
    public static void unlinkKeys(StringRedisTemplate stringRedisTemplate, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        stringRedisTemplate.unlink(keys);
        log.info("[{}] unlinked {} keys", RedisCacheEvictor.class.getSimpleName(), keys.size());
    }
}
