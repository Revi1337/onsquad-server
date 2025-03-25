package revi1337.onsquad.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.common.aspect.RequestCacheHandlerExecutionChain;

@Configuration
public class OnSquadCustomizeConfiguration {

    @Bean
    public RequestCacheHandlerExecutionChain requestCacheHandlerExecutionChain() {
        return new RequestCacheHandlerExecutionChain();
    }
}
