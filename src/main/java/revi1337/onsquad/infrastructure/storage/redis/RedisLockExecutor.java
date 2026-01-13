package revi1337.onsquad.infrastructure.storage.redis;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * Executor for distributed locking using Redis. This component ensures that a specific task is executed by only one instance at a time in a distributed
 * (scale-out) environment.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisLockExecutor {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Lua script for atomic lock release. It checks if the current value matches the expected value (ownership check) before deleting the key to prevent
     * accidental release of locks held by other instances.
     */
    private static final String LOCK_RELEASE_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * Executes the given task only if the distributed lock is successfully acquired. This method is non-blocking; if the lock is already held by another
     * instance, it skips execution and returns immediately.
     *
     * @param lockKey   Unique identifier for the lock.
     * @param leaseTime Maximum duration for which the lock is held (Auto-release time).
     * @param task      The business logic to execute upon successful lock acquisition.
     */
    public void executeIfAcquired(String lockKey, Duration leaseTime, Runnable task) {
        String lockValue = UUID.randomUUID().toString();
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, leaseTime);
        if (!Boolean.TRUE.equals(acquired)) {
            log.info("[RedisLock] Already in progress by another instance. Skipping task: {}", lockKey);
            return;
        }
        try {
            task.run();
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    /**
     * Releases the lock using a Lua script to guarantee atomicity.
     *
     * @param lockKey   Unique identifier for the lock.
     * @param lockValue The unique value assigned during acquisition to verify ownership.
     */
    private void releaseLock(String lockKey, String lockValue) {
        try {
            stringRedisTemplate.execute(
                    new DefaultRedisScript<>(LOCK_RELEASE_LUA_SCRIPT, Long.class),
                    Collections.singletonList(lockKey),
                    lockValue
            );
        } catch (Exception e) {
            log.error("[RedisLock] Unexpected error occurred during lock release for key: {}", lockKey, e);
        }
    }
}
