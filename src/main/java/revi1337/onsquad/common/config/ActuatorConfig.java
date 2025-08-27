package revi1337.onsquad.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.cache.CacheMetricsAutoConfiguration;
import org.springframework.boot.actuate.data.redis.RedisHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@EnableAutoConfiguration(exclude = {CacheMetricsAutoConfiguration.class})
@Configuration
public class ActuatorConfig {

    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

    @Bean
    public RedisHealthIndicator redisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return new RedisHealthLoggingIndicator(redisConnectionFactory);
    }

    @Slf4j
    public static class RedisHealthLoggingIndicator extends RedisHealthIndicator {

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
}
