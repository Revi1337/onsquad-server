package revi1337.onsquad.infrastructure.redis;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class RedisCacheCleaner {

    /**
     * Removes all keys from the Redis instance by executing the FLUSHALL command.
     * <p>
     * This operation deletes every key in every Redis database. Use this method with caution, as it will remove all data including caches, session information,
     * rate-limit keys, distributed locks, and any other Redis-based structures.
     * <p>
     * Recommended for local or testing environments only.
     *
     * @param stringRedisTemplate the Redis template used to obtain the connection
     */
    public static void cleanAll(StringRedisTemplate stringRedisTemplate) {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    /**
     * Scans Redis for keys matching the given pattern and deletes them using the UNLINK command.
     * <p>
     * This method leverages {@link RedisScanUtils} for safe scanning and performs a non-blocking deletion using UNLINK to avoid blocking the Redis event loop.
     *
     * @param stringRedisTemplate the Redis template used to execute scan and unlink operations
     * @param keyPattern          the Redis key pattern to match (e.g., "announce:*")
     */
    public static void cleanup(StringRedisTemplate stringRedisTemplate, String keyPattern) {
        try {
            List<String> keys = RedisScanUtils.scanKeys(stringRedisTemplate, keyPattern);
            deleteKeys(stringRedisTemplate, keys);
        } catch (RuntimeException e) {
            log.error("{}: cannot destroy redis-cache for pattern {}", RedisHealthLoggingIndicator.REDIS_HEALTH_CHECK_ERROR_LOG, keyPattern);
        }
    }

    private static void deleteKeys(StringRedisTemplate stringRedisTemplate, List<String> keys) {
        if (!keys.isEmpty()) {
            stringRedisTemplate.unlink(keys);
            log.info("[RedisCacheCleaner] deleted {} keys", keys.size());
        }
    }
}
