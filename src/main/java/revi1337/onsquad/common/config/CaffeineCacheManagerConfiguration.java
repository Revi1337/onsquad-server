package revi1337.onsquad.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@EnableCaching
@Configuration
public class CaffeineCacheManagerConfiguration {

    @Bean
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

    public static class CustomCaffeineCacheManager extends SimpleCacheManager {

        @Override
        public Cache getCache(String name) {
            String computedCacheName = String.format(CacheFormat.SIMPLE, name);
            return super.getCache(computedCacheName);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum CaffeineConst {

        CREW_ANNOUNCES(CacheConst.CREW_ANNOUNCES, Duration.ofHours(1)),
        CREW_ANNOUNCE(CacheConst.CREW_ANNOUNCE, Duration.ofHours(1)),
        CREW_STATISTIC(CacheConst.CREW_STATISTIC, Duration.ofHours(1)),
        CREW_TOP_USERS(CacheConst.CREW_TOP_USERS, Duration.ofHours(1)),
        REFRESH_TOKEN(CacheConst.CREW_TOP_USERS, Duration.ofHours(1));

        private final String cacheName;
        private final Duration expired;

        public static Stream<CaffeineConst> stream() {
            return Arrays.stream(CaffeineConst.values());
        }
    }
}
