package revi1337.onsquad.infrastructure.redis;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Configuration
public class RedisCacheManagerConfig {

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
