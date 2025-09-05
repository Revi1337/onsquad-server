package revi1337.onsquad.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public StringRedisTemplate fastStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        LettuceConnectionFactory currentConnectionFactory = (LettuceConnectionFactory) connectionFactory;
        RedisStandaloneConfiguration currentStandaloneConfiguration = currentConnectionFactory.getStandaloneConfiguration();
        RedisSentinelConfiguration currentSentinelConfiguration = currentConnectionFactory.getSentinelConfiguration();
        LettuceClientConfiguration currentClientConfiguration = currentConnectionFactory.getClientConfiguration();

        LettuceClientConfiguration fastClientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(200))
                .shutdownTimeout(currentClientConfiguration.getShutdownTimeout())
                .clientOptions(currentClientConfiguration.getClientOptions().orElseGet(ClientOptions::create))
                .clientResources(currentClientConfiguration.getClientResources().orElseGet(ClientResources::create))
                .build();

        LettuceConnectionFactory fastlettuceConnectionFactory;
        if (currentSentinelConfiguration != null) {
            fastlettuceConnectionFactory = new LettuceConnectionFactory(currentSentinelConfiguration, fastClientConfiguration);
        } else {
            fastlettuceConnectionFactory = new LettuceConnectionFactory(currentStandaloneConfiguration, fastClientConfiguration);
        }

        fastlettuceConnectionFactory.afterPropertiesSet();
        return new StringRedisTemplate(fastlettuceConnectionFactory);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        return template;
    }

    private ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
