package revi1337.onsquad.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.aspect.RedisCacheAspect;
import revi1337.onsquad.common.aspect.RequestCacheHandler;
import revi1337.onsquad.common.aspect.RequestCacheHandlerExecutionChain;
import revi1337.onsquad.common.aspect.ThrottlingAspect;

@Configuration
public class AspectConfiguration {

    @Bean
    public ThrottlingAspect throttlingAspect(RequestCacheHandler requestCacheHandler) {
        return new ThrottlingAspect(requestCacheHandler);
    }

    @Bean
    @ConditionalOnProperty(name = "onsquad.use-custom-redis-aspect", havingValue = "true")
    public RedisCacheAspect redisCacheAspect(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisCacheAspect(stringRedisTemplate, objectMapper);
    }

    @Configuration
    static class PreventDuplicateRequestConfiguration {

        @Bean
        public RequestCacheHandler requestCacheHandlerExecutionChain() {
            return new RequestCacheHandlerExecutionChain();
        }
    }
}
