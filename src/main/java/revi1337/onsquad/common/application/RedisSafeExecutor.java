package revi1337.onsquad.common.application;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.data.redis.RedisConnectionFailureException;

public class RedisSafeExecutor {

    public static <T> T supply(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RedisConnectionFailureException e) {
            throw new IllegalStateException("Redis 연결 실패", e);
        }
    }

    public static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (RedisConnectionFailureException e) {
            throw new IllegalStateException("Redis 연결 실패", e);
        }
    }

    public static <T, R> R apply(Function<T, R> function, T input) {
        try {
            return function.apply(input);
        } catch (RedisConnectionFailureException e) {
            throw new IllegalStateException("Redis 연결 실패", e);
        }
    }

    public static <T> void accept(Consumer<T> consumer, T input) {
        try {
            consumer.accept(input);
        } catch (RedisConnectionFailureException e) {
            throw new IllegalStateException("Redis 연결 실패", e);
        }
    }
}