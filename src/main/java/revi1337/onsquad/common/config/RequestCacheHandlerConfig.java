package revi1337.onsquad.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.common.aspect.ExpiringMapRequestCacheHandler;
import revi1337.onsquad.common.aspect.RedisRequestCacheHandler;
import revi1337.onsquad.common.aspect.RequestCacheHandlerExecutionChain;

@Configuration
public class RequestCacheHandlerConfig {

    @Bean
    public RequestCacheHandlerExecutionChain requestCacheHandlerExecutionChain(
            RedisRequestCacheHandler redisRequestCacheHandler,
            ExpiringMapRequestCacheHandler expiringMapRequestCacheHandler
    ) {
        RequestCacheHandlerExecutionChain handlerExecutionChain = new RequestCacheHandlerExecutionChain();
        handlerExecutionChain.addFirst(redisRequestCacheHandler);
        handlerExecutionChain.addLast(expiringMapRequestCacheHandler);
        return handlerExecutionChain;
    }
}
