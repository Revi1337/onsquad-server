package revi1337.onsquad.infrastructure.storage.redis;

import static lombok.AccessLevel.PRIVATE;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class RedisScanUtils {

    /**
     * Scans Redis for all keys matching the given pattern using the {@link ScanSize#DEFAULT}.
     * <p>
     * This is a convenience wrapper for {@link #scanKeys(StringRedisTemplate, String, ScanSize)}. Recommended for small to medium-sized datasets where standard
     * performance is expected.
     *
     * @param stringRedisTemplate the Redis template to execute commands
     * @param pattern             the key pattern to match (e.g., "crew:*")
     * @return a list of matching keys
     */
    public static List<String> scanKeys(StringRedisTemplate stringRedisTemplate, String pattern) {
        return scanKeys(stringRedisTemplate, pattern, ScanSize.DEFAULT);
    }

    /**
     * Scans Redis for keys matching the given pattern based on the specified {@link ScanSize}.
     * <p>
     * The {@code scanSize} determines the range of hash slots Redis inspects in a single iteration. Note that this is a <b>hint</b> for the work performed by
     * the Redis server, not a limit on the number of keys returned in the final list.
     *
     * @param stringRedisTemplate the Redis template to execute commands
     * @param pattern             the key pattern to match
     * @param scanSize            the strategy to balance Redis CPU load and network RTT (LIGHT, DEFAULT, BULK)
     * @return a list of matching keys
     */
    public static List<String> scanKeys(StringRedisTemplate stringRedisTemplate, String pattern, ScanSize scanSize) {
        List<String> keys = new ArrayList<>();
        scan(stringRedisTemplate, pattern, scanSize, keys::add);
        return keys;
    }

    /**
     * Scans multiple patterns and returns a consolidated list of unique matching keys.
     * <p>
     * This method performs an individual SCAN operation for each pattern and flattens the results. Total execution time increases linearly with the number of
     * patterns (N * RTT). All scans utilize the {@link ScanSize#DEFAULT} for system stability.
     *
     * @param stringRedisTemplate the Redis template to execute commands
     * @param patterns            the list of key patterns to match (e.g., ["crew:*", "squad:*"])
     * @return a consolidated list of unique matching keys
     */
    public static List<String> scanKeys(StringRedisTemplate stringRedisTemplate, List<String> patterns) {
        return patterns.stream()
                .flatMap(pattern -> scanKeys(stringRedisTemplate, pattern, ScanSize.DEFAULT).stream())
                .distinct()
                .toList();
    }

    /**
     * Executes the Redis SCAN command and performs the given {@link Consumer} action for each matching key.
     * <p>
     * <b>Implementation Details:</b>
     * <ul>
     * <li>Uses {@link RedisCallback} directly to ensure efficient Redis connection pool management.</li>
     * <li>Utilizes a try-with-resources block with {@link Cursor} to prevent connection leaks by ensuring the cursor is closed after iteration.</li>
     * <li>Gracefully handles exceptions by logging errors, ensuring that a single scan failure does not crash the application.</li>
     * </ul>
     *
     * @param stringRedisTemplate the Redis template to execute commands
     * @param pattern             the key pattern to match
     * @param scanSize            the hint for the number of elements to process per iteration
     * @param consumer            the action to be performed for each discovered key
     */
    private static void scan(StringRedisTemplate stringRedisTemplate, String pattern, ScanSize scanSize, Consumer<String> consumer) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(scanSize.getCount())
                .build();

        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                while (cursor.hasNext()) {
                    consumer.accept(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error("Redis scan execution failed. pattern: {}, count: {}", pattern, scanSize.getCount(), e);
            }
            return null;
        });
    }

    /**
     * Defines the COUNT hint for Redis SCAN iterations.
     * <p>
     * <b>Warning:</b> This value does not act as a LIMIT on the total result set. It represents
     * the number of hash slots Redis traverses per request to find matching keys.
     */
    @Getter
    @RequiredArgsConstructor
    public enum ScanSize {

        LIGHT(100L), DEFAULT(250L), MIDDLE(500L), BULK(1000L);

        private final long count;

    }
}
