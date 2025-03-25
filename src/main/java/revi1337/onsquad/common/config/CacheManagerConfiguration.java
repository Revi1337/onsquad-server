package revi1337.onsquad.common.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@EnableCaching
@Configuration
public class CacheManagerConfiguration {

    @Configuration
    static class CaffeineCacheManagerConfiguration {

        @Primary
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

        private static class CustomCaffeineCacheManager extends SimpleCacheManager {

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

    @Configuration
    @ConditionalOnProperty(name = "onsquad.use-redis-cache-manager", havingValue = "true")
    static class RedisCacheManagerConfiguration {

        @Bean
        public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(defaultConfigurationWithoutDefaultTyping())
                    .withInitialCacheConfigurations(initConfiguration())
                    .build();
        }

        private Map<String, RedisCacheConfiguration> initConfiguration() {
            return new HashMap<>() {{
                put(CacheConst.CREW_STATISTIC, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
                put(CacheConst.CREW_ANNOUNCES, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
                put(CacheConst.CREW_ANNOUNCE, defaultConfigurationWithoutDefaultTyping().entryTtl(Duration.ofHours(1)));
                put(CacheConst.CREW_TOP_USERS, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
            }};
        }

        private RedisCacheConfiguration defaultConfigurationWithoutDefaultTyping() {
            GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
            serializer.configure(objectMapper -> objectMapper.registerModule(new JavaTimeModule()));

            return RedisCacheConfiguration.defaultCacheConfig()
                    .computePrefixWith(cacheName -> String.format(CacheFormat.PREFIX, cacheName))
                    .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(fromSerializer(serializer));
        }

        private RedisCacheConfiguration defaultConfigurationWithDefaultTyping() {
            ObjectMapper collectionObjectMapper = buildCollectionObjectMapper();
            GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
                    collectionObjectMapper);

            return defaultConfigurationWithoutDefaultTyping()
                    .serializeValuesWith(fromSerializer(serializer));
        }

        private ObjectMapper buildCollectionObjectMapper() {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .activateDefaultTyping(
                            BasicPolymorphicTypeValidator.builder()
                                    .allowIfBaseType(Object.class)
                                    .build(),
                            DefaultTyping.NON_FINAL
                    );
        }
    }
}
