package revi1337.onsquad.infrastructure.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisLockExecutor {

    private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofMinutes(10);

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Executes the task with a distributed lock using the specified lease time. The lock is automatically released after the lease time even if the application
     * terminates unexpectedly.
     *
     * @param lockKey   The unique key for the lock
     * @param leaseTime The duration for which the lock is held (Auto-release time for safety)
     * @param task      The business logic to execute
     */
    public void executeWithLock(String lockKey, Duration leaseTime, Runnable task) {
        execute(lockKey, leaseTime, task);
    }

    /**
     * Executes the task with a distributed lock using the default lease time (10 minutes). The lock is automatically released after the lease time even if the
     * application terminates unexpectedly.
     *
     * @param lockKey The unique key for the lock
     * @param task    The business logic to execute
     */
    public void executeWithLock(String lockKey, Runnable task) {
        execute(lockKey, DEFAULT_LOCK_TIMEOUT, task);
    }

    private void execute(String lockKey, Duration leaseTime, Runnable task) {
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "locked", leaseTime);
        if (!Boolean.TRUE.equals(acquired)) {
            return;
        }
        try {
            task.run();
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }
}
