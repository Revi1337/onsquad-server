package revi1337.onsquad.infrastructure.redis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
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
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    /**
     * Scans Redis for keys matching the given pattern and deletes them using the UNLINK command.
     * <p>
     * This method performs a non-blocking deletion of keys by first scanning Redis using the SCAN command and then calling UNLINK, which unlinks keys
     * asynchronously to avoid blocking Redis during large deletions.
     * <p>
     * Suitable for removing keys belonging to a specific cache namespace without affecting unrelated data.
     *
     * @param stringRedisTemplate the Redis template used to execute scan and unlink operations
     * @param keyPattern          the Redis key pattern to match (e.g., "announce:*")
     */
    public static void cleanup(StringRedisTemplate stringRedisTemplate, String keyPattern) {
        try (Cursor<byte[]> scanCursor = getRedisCursor(stringRedisTemplate, keyPattern)) {
            deleteKeys(stringRedisTemplate, getKeysForDeletion(scanCursor));
        } catch (RuntimeException e) {
            log.error("{}: cannot destroy redis-cache for pattern {}", RedisHealthLoggingIndicator.REDIS_HEALTH_CHECK_ERROR_LOG, keyPattern);
        }
    }

    private static Cursor<byte[]> getRedisCursor(StringRedisTemplate stringRedisTemplate, String keyPattern) {
        return stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions()
                        .match(keyPattern)
                        .build()
                );
    }

    private static List<String> getKeysForDeletion(Cursor<byte[]> scanCursor) {
        List<String> keys = new ArrayList<>();
        while (scanCursor.hasNext()) {
            keys.add(new String(scanCursor.next(), StandardCharsets.UTF_8));
        }
        return keys;
    }

    private static void deleteKeys(StringRedisTemplate stringRedisTemplate, List<String> keys) {
        if (!keys.isEmpty()) {
            stringRedisTemplate.unlink(keys);
            log.info("[RedisCacheCleaner] deleted {} keys", keys.size());
        }
    }
}
