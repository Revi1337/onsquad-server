package revi1337.onsquad.infrastructure.support.request;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.aspect.RequestCacheHandler;
import revi1337.onsquad.common.aspect.RequestCacheHandlerChain;

/**
 * RedisRequestCacheHandler For RequestCacheHandlerExecutionChain
 *
 * @see RequestCacheHandlerChain
 */
@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class RedisRequestCacheHandler implements RequestCacheHandler {

    public static final String EXCEPTION_LOG_FORMAT = "[Exception 발생: {} - Message: {}] ";
    private static final String REDIS_THROTTLE_CIRCUIT_BREAKER_NAME = "redisThrottleCircuitBreaker";

    private final StringRedisTemplate fastStringRedisTemplate;

    @CircuitBreaker(name = REDIS_THROTTLE_CIRCUIT_BREAKER_NAME, fallbackMethod = "fallback")
    @Override
    public Boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        try {
            var valueOperations = fastStringRedisTemplate.opsForValue();
            return valueOperations.setIfAbsent(key, value, timeout, unit);
        } catch (DataAccessException exception) {
            log.error(EXCEPTION_LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
            throw exception;
        }
    }

    private Boolean fallback(String key, String value, long timeout, TimeUnit unit, Exception exception) {
        throw new IllegalStateException("Redis 사용 불가", exception);
    }
}
