package revi1337.onsquad.infrastructure.storage.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Configuration
public class CaffeineCacheManagerConfig {

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CustomCaffeineCacheManager cacheManager = new CustomCaffeineCacheManager();
        List<String> cacheNames = CaffeineConst.stream()
                .map(caffeineConst -> String.format(CacheFormat.SIMPLE, caffeineConst.getCacheName()))
                .toList();
        cacheManager.setCacheNames(cacheNames);

        CaffeineConst.stream().forEach(caffeineConst -> {
            String cacheName = String.format(CacheFormat.SIMPLE, caffeineConst.getCacheName());
            cacheManager.registerCustomCache(cacheName,
                    Caffeine.newBuilder()
                            .recordStats()
                            .expireAfterWrite(caffeineConst.getExpired())
                            .build());
        });

        return cacheManager;
    }

    private static class CustomCaffeineCacheManager extends CaffeineCacheManager {

        @Override
        public Cache getCache(String name) {
            String computedCacheName = String.format(CacheFormat.SIMPLE, name);
            return super.getCache(computedCacheName);
        }
    }
}
