package revi1337.onsquad.common;

import com.redis.testcontainers.RedisContainer;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;

public interface RedisTestContainerSupport {

    RedisContainer REDIS = createAndStartRedis();

    private static RedisContainer createAndStartRedis() {
        RedisContainer container = new RedisContainer(DockerImageName.parse("redis:7.0.8-alpine"));
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getFirstMappedPort().toString());
    }

    default void flushRedis(StringRedisTemplate redisTemplate) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }
}
