package revi1337.onsquad.common.config;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;
import static revi1337.onsquad.common.constant.CacheConst.CREW_STATISTIC;
import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@TestConfiguration
public class ApplicationLayerConfiguration {

    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new DefaultHandlerExceptionResolver();
    }

    @Bean
    public CacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager(
                CREW_ANNOUNCES,
                CREW_ANNOUNCE,
                CREW_STATISTIC,
                CREW_TOP_USERS,
                CREW_TOP_USERS
        );
    }
}
