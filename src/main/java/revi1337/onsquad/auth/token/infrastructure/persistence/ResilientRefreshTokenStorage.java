package revi1337.onsquad.auth.token.infrastructure.persistence;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.token.application.RefreshTokenStorage;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;

@Slf4j
@Primary
@Component
public class ResilientRefreshTokenStorage implements RefreshTokenStorage {

    private static final String REDIS_AUTH_CIRCUIT_BREAKER_NAME = "redisAuthCircuitBreaker";
    private static final String REDIS_ERROR_LOG_FORMAT = "[Refresh Token Redis {} 실패, RDB 로 폴백] {}: {}";
    private static final String REDIS_BLOCKED_LOG_FORMAT = "[Refresh Token Redis {} 차단, RDB 로 폴백] {}";
    private static final String RDB_ERROR_LOG_FORMAT = "[Refresh Token RDB {} 실패] {}: {}";

    private final RefreshTokenStorage redisRefreshTokenStorage;
    private final RefreshTokenStorage rdbRefreshTokenStorage;
    private final CircuitBreaker circuitBreaker;

    public ResilientRefreshTokenStorage(
            @Qualifier("redisRefreshTokenStorage") RefreshTokenStorage redisRefreshTokenStorage,
            @Qualifier("rdbRefreshTokenStorage") RefreshTokenStorage rdbRefreshTokenStorage,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.redisRefreshTokenStorage = redisRefreshTokenStorage;
        this.rdbRefreshTokenStorage = rdbRefreshTokenStorage;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(REDIS_AUTH_CIRCUIT_BREAKER_NAME);
    }

    @Override
    public long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration) {
        RedisOutcome<Long> redisOutcome = callRedis("save", () -> redisRefreshTokenStorage.saveToken(memberId, refreshToken, expireDuration));
        try {
            long rdbResult = rdbRefreshTokenStorage.saveToken(memberId, refreshToken, expireDuration);
            return redisOutcome.succeeded() ? redisOutcome.value() : rdbResult;
        } catch (RuntimeException rdbFailure) {
            return handleRdbFailure("save", rdbFailure, redisOutcome, redisOutcome.value());
        }
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        RedisOutcome<Optional<RefreshToken>> redisOutcome = callRedis("find", () -> redisRefreshTokenStorage.findTokenBy(memberId));
        if (redisOutcome.succeeded() && redisOutcome.value() != null && redisOutcome.value().isPresent()) {
            return redisOutcome.value();
        }
        try {
            return rdbRefreshTokenStorage.findTokenBy(memberId);
        } catch (RuntimeException rdbFailure) {
            return handleRdbFailure("find", rdbFailure, redisOutcome, Optional.empty());
        }
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        RedisOutcome<Void> redisOutcome = callRedis("delete", () -> {
            redisRefreshTokenStorage.deleteTokenBy(memberId);
            return null;
        });
        try {
            rdbRefreshTokenStorage.deleteTokenBy(memberId);
        } catch (RuntimeException rdbFailure) {
            handleRdbFailure("delete", rdbFailure, redisOutcome, null);
        }
    }

    @Override
    public void deleteAll() {
        RedisOutcome<Void> redisOutcome = callRedis("deleteAll", () -> {
            redisRefreshTokenStorage.deleteAll();
            return null;
        });
        try {
            rdbRefreshTokenStorage.deleteAll();
        } catch (RuntimeException rdbFailure) {
            handleRdbFailure("deleteAll", rdbFailure, redisOutcome, null);
        }
    }

    private <T> RedisOutcome<T> callRedis(String operation, Supplier<T> redisCall) {
        try {
            return new RedisOutcome<>(circuitBreaker.executeSupplier(redisCall), null);
        } catch (CallNotPermittedException exception) {
            log.warn(REDIS_BLOCKED_LOG_FORMAT, operation, exception.getMessage());
            return new RedisOutcome<>(null, exception);
        } catch (RuntimeException exception) {
            log.error(REDIS_ERROR_LOG_FORMAT, operation, exception.getClass().getSimpleName(), exception.getMessage(), exception);
            return new RedisOutcome<>(null, exception);
        }
    }

    private <T> T handleRdbFailure(String operation, RuntimeException rdbFailure, RedisOutcome<?> redisOutcome, T valueWhenRedisSucceeded) {
        if (redisOutcome.succeeded()) {
            log.error(RDB_ERROR_LOG_FORMAT, operation, rdbFailure.getClass().getSimpleName(), rdbFailure.getMessage(), rdbFailure);
            return valueWhenRedisSucceeded;
        }
        if (rdbFailure != redisOutcome.failure()) {
            rdbFailure.addSuppressed(redisOutcome.failure());
        }
        throw rdbFailure;
    }

    private record RedisOutcome<T>(T value, RuntimeException failure) {

        boolean succeeded() {
            return failure == null;
        }
    }
}
