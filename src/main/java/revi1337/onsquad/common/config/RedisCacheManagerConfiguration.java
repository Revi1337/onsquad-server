package revi1337.onsquad.common.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisCacheManagerConfiguration {

    private static final String DEFAULT_KEY_FORMAT = "onsquad:%s:";

    @Primary
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfiguration())
                .withInitialCacheConfigurations(initConfiguration())
                .build();
    }

    private Map<String, RedisCacheConfiguration> initConfiguration() {
        return new HashMap<>() {{
            put(RedisCacheName.CREW_STATISTIC, defaultConfiguration().entryTtl(Duration.ofHours(1)));
        }};
    }

    private RedisCacheConfiguration defaultConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> String.format(DEFAULT_KEY_FORMAT, cacheName))
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    abstract public static class RedisCacheName {

        public static final String CREW_STATISTIC = "crew-statistic";
    }
}
