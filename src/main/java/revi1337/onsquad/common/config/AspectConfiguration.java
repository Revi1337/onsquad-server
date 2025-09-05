package revi1337.onsquad.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.aspect.RedisCacheAspect;
import revi1337.onsquad.common.aspect.RequestCacheHandlerExecutionChain;
import revi1337.onsquad.common.aspect.ThrottlingAspect;

@Configuration
public class AspectConfiguration {

    @Bean
    public RedisCacheAspect redisCacheAspect(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisCacheAspect(stringRedisTemplate, objectMapper);
    }

    @Bean
    public ThrottlingAspect throttlingAspect(RequestCacheHandlerExecutionChain requestCacheHandler) {
        return new ThrottlingAspect(requestCacheHandler);
    }
}
