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
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

@Slf4j
@Primary
@Component
public class ResilientVerificationCodeStorage implements VerificationCodeStorage {

    private static final String REDIS_AUTH_CIRCUIT_BREAKER_NAME = "redisAuthCircuitBreaker";
    private static final String STORAGE_ERROR_LOG_FORMAT = "[Verification Code Redis {} 실패, RDB 로 폴백] {}: {}";
    private static final String STORAGE_BLOCKED_LOG_FORMAT = "[Verification Code Redis {} 차단, RDB 로 폴백] {}";

    private final VerificationCodeStorage redisVerificationCodeStorage;
    private final VerificationCodeStorage rdbVerificationCodeStorage;
    private final CircuitBreaker circuitBreaker;

    public ResilientVerificationCodeStorage(
            @Qualifier("redisVerificationCodeStorage") VerificationCodeStorage redisVerificationCodeStorage,
            @Qualifier("rdbVerificationCodeStorage") VerificationCodeStorage rdbVerificationCodeStorage,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.redisVerificationCodeStorage = redisVerificationCodeStorage;
        this.rdbVerificationCodeStorage = rdbVerificationCodeStorage;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(REDIS_AUTH_CIRCUIT_BREAKER_NAME);
    }

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration expireDuration) {
        return execute(
                "save",
                () -> redisVerificationCodeStorage.saveVerificationCode(email, code, status, expireDuration),
                () -> rdbVerificationCodeStorage.saveVerificationCode(email, code, status, expireDuration)
        );
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        return execute(
                "validate",
                () -> redisVerificationCodeStorage.isValidVerificationCode(email, code),
                () -> rdbVerificationCodeStorage.isValidVerificationCode(email, code)
        );
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus status, Duration expireDuration) {
        return execute(
                "mark",
                () -> redisVerificationCodeStorage.markVerificationStatus(email, status, expireDuration),
                () -> rdbVerificationCodeStorage.markVerificationStatus(email, status, expireDuration)
        );
    }

    @Override
    public boolean markVerificationStatusAsSuccess(String email, String authCode, Duration expireDuration) {
        return execute(
                "markAsSuccess",
                () -> redisVerificationCodeStorage.markVerificationStatusAsSuccess(email, authCode, expireDuration),
                () -> rdbVerificationCodeStorage.markVerificationStatusAsSuccess(email, authCode, expireDuration)
        );
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus status) {
        return execute(
                "isMarked",
                () -> redisVerificationCodeStorage.isMarkedVerificationStatusWith(email, status),
                () -> rdbVerificationCodeStorage.isMarkedVerificationStatusWith(email, status)
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
