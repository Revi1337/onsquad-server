package revi1337.onsquad.common.config.system;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheErrorHandler;

@EnableCaching
@Configuration
public class CacheManagerConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }
}
