package revi1337.onsquad.common.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
                .cacheDefaults(defaultConfigurationWithoutDefaultTyping())
                .withInitialCacheConfigurations(initConfiguration())
                .build();
    }

    private Map<String, RedisCacheConfiguration> initConfiguration() {
        return new HashMap<>() {{
            put(RedisCacheName.CREW_STATISTIC, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
            put(RedisCacheName.CREW_ANNOUNCES, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
            put(RedisCacheName.CREW_ANNOUNCE, defaultConfigurationWithoutDefaultTyping().entryTtl(Duration.ofHours(1)));
            put(RedisCacheName.CREW_TOP_USERS, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
        }};
    }

    private RedisCacheConfiguration defaultConfigurationWithoutDefaultTyping() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        serializer.configure(objectMapper -> objectMapper.registerModule(new JavaTimeModule()));

        return RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> String.format(DEFAULT_KEY_FORMAT, cacheName))
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(fromSerializer(serializer));
    }

    private RedisCacheConfiguration defaultConfigurationWithDefaultTyping() {
        ObjectMapper collectionObjectMapper = buildCollectionObjectMapper();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(collectionObjectMapper);

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

    abstract public static class RedisCacheName {

        public static final String CREW_STATISTIC = "crew-statistic";
        public static final String CREW_ANNOUNCES = "crew-announces";
        public static final String CREW_ANNOUNCE = "crew-announce";
        public static final String CREW_TOP_USERS = "crew-top-users";

    }
}
