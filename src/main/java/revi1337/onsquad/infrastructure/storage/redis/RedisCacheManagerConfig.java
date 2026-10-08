package revi1337.onsquad.infrastructure.storage.redis;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Configuration
public class RedisCacheManagerConfig {

    private static final String REDIS_CACHE_CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";

    private final ObjectMapper collectionObjectMapper;

    public RedisCacheManagerConfig(@Qualifier("collectionObjectMapper") ObjectMapper collectionObjectMapper) {
        this.collectionObjectMapper = collectionObjectMapper;
    }

    @Bean
    public CircuitBreakerRedisCacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        return new CircuitBreakerRedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                defaultConfigurationWithoutDefaultTyping(),
                initConfiguration(),
                circuitBreakerRegistry.circuitBreaker(REDIS_CACHE_CIRCUIT_BREAKER_NAME)
        );
    }

    private Map<String, RedisCacheConfiguration> initConfiguration() {
        return new HashMap<>() {{
            put(CacheConst.CREW_STATISTIC, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
            put(CacheConst.CREW_ANNOUNCES, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
            put(CacheConst.CREW_ANNOUNCE, defaultConfigurationWithoutDefaultTyping().entryTtl(Duration.ofHours(1)));
            put(CacheConst.CREW_RANK_MEMBERS, defaultConfigurationWithDefaultTyping().entryTtl(Duration.ofHours(1)));
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
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(collectionObjectMapper);

        return defaultConfigurationWithoutDefaultTyping()
                .serializeValuesWith(fromSerializer(serializer));
    }
}
