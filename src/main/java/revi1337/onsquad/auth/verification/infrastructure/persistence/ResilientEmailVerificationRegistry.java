package revi1337.onsquad.auth.verification.infrastructure.persistence;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.application.EmailVerificationRegistry;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

@Slf4j
@Primary
@Component
public class ResilientEmailVerificationRegistry implements EmailVerificationRegistry {

    private static final String REDIS_AUTH_CIRCUIT_BREAKER_NAME = "redisAuthCircuitBreaker";
    private static final String STORAGE_ERROR_LOG_FORMAT = "[Verification Code Redis {} 실패, RDB 로 폴백] {}: {}";
    private static final String STORAGE_BLOCKED_LOG_FORMAT = "[Verification Code Redis {} 차단, RDB 로 폴백] {}";

    private final EmailVerificationRegistry redisEmailVerificationRegistry;
    private final EmailVerificationRegistry rdbEmailVerificationRegistry;
    private final CircuitBreaker circuitBreaker;

    public ResilientEmailVerificationRegistry(
            @Qualifier("redisEmailVerificationRegistry") EmailVerificationRegistry redisEmailVerificationRegistry,
            @Qualifier("rdbEmailVerificationRegistry") EmailVerificationRegistry rdbEmailVerificationRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.redisEmailVerificationRegistry = redisEmailVerificationRegistry;
        this.rdbEmailVerificationRegistry = rdbEmailVerificationRegistry;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(REDIS_AUTH_CIRCUIT_BREAKER_NAME);
    }

    @Override
    public long register(String email, String code, VerificationStatus status, Duration expireDuration) {
        return execute(
                "save",
                () -> redisEmailVerificationRegistry.register(email, code, status, expireDuration),
                () -> rdbEmailVerificationRegistry.register(email, code, status, expireDuration)
        );
    }

    @Override
    public boolean isCodeMatched(String email, String code) {
        return execute(
                "validate",
                () -> redisEmailVerificationRegistry.isCodeMatched(email, code),
                () -> rdbEmailVerificationRegistry.isCodeMatched(email, code)
        );
    }

    @Override
    public boolean changeStatus(String email, VerificationStatus status, Duration expireDuration) {
        return execute(
                "mark",
                () -> redisEmailVerificationRegistry.changeStatus(email, status, expireDuration),
                () -> rdbEmailVerificationRegistry.changeStatus(email, status, expireDuration)
        );
    }

    @Override
    public boolean confirm(String email, String authCode, Duration expireDuration) {
        return execute(
                "markAsSuccess",
                () -> redisEmailVerificationRegistry.confirm(email, authCode, expireDuration),
                () -> rdbEmailVerificationRegistry.confirm(email, authCode, expireDuration)
        );
    }

    @Override
    public boolean hasStatus(String email, VerificationStatus status) {
        return execute(
                "isMarked",
                () -> redisEmailVerificationRegistry.hasStatus(email, status),
                () -> rdbEmailVerificationRegistry.hasStatus(email, status)
        );
    }

    private <T> T execute(String operation, Supplier<T> redisCall, Supplier<T> rdbCall) {
        try {
            return circuitBreaker.executeSupplier(redisCall);
        } catch (CallNotPermittedException exception) {
            log.warn(STORAGE_BLOCKED_LOG_FORMAT, operation, exception.getMessage());
        } catch (RuntimeException exception) {
            log.error(STORAGE_ERROR_LOG_FORMAT, operation, exception.getClass().getSimpleName(), exception.getMessage(), exception);
        }
        return rdbCall.get();
    }
}
