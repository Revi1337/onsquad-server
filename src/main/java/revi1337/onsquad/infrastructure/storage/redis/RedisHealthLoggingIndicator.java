package revi1337.onsquad.infrastructure.storage.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.data.redis.RedisHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisHealthLoggingIndicator extends RedisHealthIndicator {

    private static final String REDIS_HEALTH_CHECK_SUCCESS_LOG = "Redis connection success";
    public static final String REDIS_HEALTH_CHECK_ERROR_LOG = "Redis connection failed";

    public RedisHealthLoggingIndicator(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            super.doHealthCheck(builder);
            log.debug(REDIS_HEALTH_CHECK_SUCCESS_LOG);
            builder.up();
        } catch (Exception e) {
            log.error(REDIS_HEALTH_CHECK_ERROR_LOG);
            builder.down().withDetail("error", e.getMessage());
        }
    }
}
