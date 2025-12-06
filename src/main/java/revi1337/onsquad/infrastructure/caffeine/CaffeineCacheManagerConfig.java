package revi1337.onsquad.infrastructure.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Configuration
public class CaffeineCacheManagerConfig {

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        List<CaffeineCache> caches = CaffeineConst.stream()
                .map(caffeineConst -> new CaffeineCache(
                        String.format(CacheFormat.SIMPLE, caffeineConst.getCacheName()),
                        Caffeine.newBuilder().recordStats()
                                .expireAfterWrite(caffeineConst.getExpired())
                                .build()))
                .toList();

        SimpleCacheManager simpleCacheManager = new CustomCaffeineCacheManager();
        simpleCacheManager.setCaches(caches);

        return simpleCacheManager;
    }

    private static class CustomCaffeineCacheManager extends SimpleCacheManager {

        @Override
        public Cache getCache(String name) {
            String computedCacheName = String.format(CacheFormat.SIMPLE, name);
            return super.getCache(computedCacheName);
        }
    }
}
