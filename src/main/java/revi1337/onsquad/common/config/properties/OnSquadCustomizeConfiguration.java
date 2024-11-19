package revi1337.onsquad.common.config.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.aspect.ExpiredMapRequestCacheHandler;
import revi1337.onsquad.common.aspect.RedisRequestCacheHandler;
import revi1337.onsquad.common.aspect.RequestCacheHandlerExecutionChain;

@Configuration
public class OnSquadCustomizeConfiguration {

    @Bean
    public RequestCacheHandlerExecutionChain requestCacheHandlerExecutionChain(StringRedisTemplate stringRedisTemplate) {
        RequestCacheHandlerExecutionChain handlerExecutionChain = new RequestCacheHandlerExecutionChain();
        handlerExecutionChain.addRequestCacheHandlerBefore(
                new RedisRequestCacheHandler(stringRedisTemplate),
                ExpiredMapRequestCacheHandler.class
        );

        return handlerExecutionChain;
    }
}
