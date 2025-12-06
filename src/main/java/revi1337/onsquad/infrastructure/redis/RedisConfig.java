package revi1337.onsquad.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Configures a {@link RedisTemplate} for general-purpose usage.
     * <p>
     * Keys are serialized as Strings, and values (including hash values) are serialized using a {@link GenericJackson2JsonRedisSerializer} backed by the
     * provided {@link ObjectMapper}.
     *
     * @param connectionFactory   the {@link RedisConnectionFactory} to use
     * @param defaultObjectMapper the {@link ObjectMapper} for value serialization
     * @return a fully configured {@link RedisTemplate} instance
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       @Qualifier("defaultObjectMapper") ObjectMapper defaultObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(defaultObjectMapper);
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        return template;
    }

    /**
     * Provides a standard {@link StringRedisTemplate} for simple String operations.
     *
     * @param connectionFactory the {@link RedisConnectionFactory} to use
     * @return a {@link StringRedisTemplate} instance
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * Provides a {@link StringRedisTemplate} with a short command timeout for fast operations.
     * <p>
     * Useful for caching or scenarios where Redis commands need to fail quickly.
     *
     * @param connectionFactory the base {@link RedisConnectionFactory} to copy configuration from
     * @return a {@link StringRedisTemplate} with 200ms command timeout
     */
    @Bean
    public StringRedisTemplate fastStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        LettuceConnectionFactory currentConnectionFactory = (LettuceConnectionFactory) connectionFactory;
        LettuceClientConfiguration fastClientConfiguration = copyLettuceClientConfigurationWithTimeout(currentConnectionFactory, Duration.ofMillis(200));
        LettuceConnectionFactory fastLettuceConnectionFactory = replicateFactoryPreservingMode(currentConnectionFactory, fastClientConfiguration);
        fastLettuceConnectionFactory.afterPropertiesSet();
        return new StringRedisTemplate(fastLettuceConnectionFactory);
    }

    /**
     * Creates a copy of the {@link LettuceClientConfiguration} from the original factory, overriding the command timeout.
     *
     * @param originalFactory the original {@link LettuceConnectionFactory} to copy from
     * @param timeout         the new command timeout to apply
     * @return a new {@link LettuceClientConfiguration} instance
     */
    private LettuceClientConfiguration copyLettuceClientConfigurationWithTimeout(LettuceConnectionFactory originalFactory, Duration timeout) {
        LettuceClientConfiguration baseConfiguration = originalFactory.getClientConfiguration();
        return LettuceClientConfiguration.builder()
                .commandTimeout(timeout)
                .shutdownTimeout(baseConfiguration.getShutdownTimeout())
                .clientOptions(baseConfiguration.getClientOptions().orElseGet(ClientOptions::create))
                .clientResources(baseConfiguration.getClientResources().orElseGet(ClientResources::create))
                .build();
    }

    /**
     * Creates a new {@link LettuceConnectionFactory} using the same type (Sentinel or Standalone) as the original factory, but applies a different
     * {@link LettuceClientConfiguration}.
     *
     * @param originalFactory the original {@link LettuceConnectionFactory} to replicate
     * @param newClientConfig the new {@link LettuceClientConfiguration} to apply
     * @return a new {@link LettuceConnectionFactory} instance preserving the mode (Sentinel/Standalone)
     */
    private LettuceConnectionFactory replicateFactoryPreservingMode(LettuceConnectionFactory originalFactory, LettuceClientConfiguration newClientConfig) {
        if (originalFactory.getSentinelConfiguration() != null) {
            return new LettuceConnectionFactory(originalFactory.getSentinelConfiguration(), newClientConfig);
        }
        return new LettuceConnectionFactory(originalFactory.getStandaloneConfiguration(), newClientConfig);
    }
}
