package revi1337.onsquad.infrastructure.redis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisScanUtils {

    public static final long DEFAULT_SCAN_SIZE = 200;

    /**
     * Scans Redis for all keys matching the given pattern and returns them as a list.
     * <p>
     * This method is a convenience wrapper around {@link #scan(StringRedisTemplate, String, Long, Consumer)}. Use this for small to medium-sized datasets where
     * memory overhead is not a concern.
     *
     * @param stringRedisTemplate the Redis template to use
     * @param pattern             the key pattern to match (e.g., "crew:*")
     * @return a list of matching keys
     */
    public static List<String> scanKeys(StringRedisTemplate stringRedisTemplate, String pattern) {
        return scanKeys(stringRedisTemplate, pattern, null);
    }

    /**
     * Scans Redis for keys matching the given pattern with a specified hint count and returns them as a list.
     *
     * @param stringRedisTemplate the Redis template to use
     * @param pattern             the key pattern to match
     * @param count               the hint for the number of elements to return per scan iteration
     * @return a list of matching keys
     */
    public static List<String> scanKeys(StringRedisTemplate stringRedisTemplate, String pattern, Long count) {
        List<String> keys = new ArrayList<>();
        scan(stringRedisTemplate, pattern, count, keys::add);
        return keys;
    }

    /**
     * Scans Redis for keys matching the given pattern and performs the given action for each key.
     * <p>
     * This method uses the default Redis scan count.
     *
     * @param stringRedisTemplate the Redis template to use
     * @param pattern             the key pattern to match
     * @param consumer            the action to be performed for each matching key
     */
    public static void scan(StringRedisTemplate stringRedisTemplate, String pattern, Consumer<String> consumer) {
        scan(stringRedisTemplate, pattern, null, consumer);
    }

    /**
     * Scans Redis for keys matching the given pattern and performs the given action for each key.
     * <p>
     * This method executes the SCAN command via {@link RedisCallback} to ensure that the Redis connection is properly managed and released back to the pool. It
     * uses {@link Cursor} within a try-with-resources block to prevent resource leaks.
     *
     * @param stringRedisTemplate the Redis template to use
     * @param pattern             the key pattern to match
     * @param count               optional hint for the number of elements to return per scan iteration (null for default)
     * @param consumer            the action to be performed for each matching key
     * @throws RuntimeException if an error occurs during the scan execution
     */
    public static void scan(StringRedisTemplate stringRedisTemplate, String pattern, Long count, Consumer<String> consumer) {
        ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions().match(pattern);
        if (count != null) {
            builder.count(count);
        }
        ScanOptions options = builder.build();

        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                while (cursor.hasNext()) {
                    consumer.accept(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error("Redis scan execution failed. pattern: {}", pattern, e);
            }
            return null;
        });
    }
}
