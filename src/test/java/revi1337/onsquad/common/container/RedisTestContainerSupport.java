package revi1337.onsquad.common.container;

import com.redis.testcontainers.RedisContainer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = RedisTestContainerSupport.RedisInitializer.class)
public abstract class RedisTestContainerSupport {

    public static class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            RedisContainer redis = TestContainerRegistry.getRedis();
            Map<String, String> properties = new HashMap<>();
            properties.put("spring.data.redis.host", redis.getHost());
            properties.put("spring.data.redis.port", redis.getFirstMappedPort().toString());
            TestPropertyValues.of(properties).applyTo(applicationContext);
        }
    }
}

//package revi1337.onsquad.common.container;
//
//import com.redis.testcontainers.RedisContainer;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.utility.DockerImageName;
//
//public interface RedisTestContainerSupport {
//
//    RedisContainer REDIS = RedisContainerHolder.getInstance();
//
//    default void flushRedis(StringRedisTemplate redisTemplate) {
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.serverCommands().flushAll();
//            return null;
//        });
//    }
//
//    default void flushRedis(RedisTemplate<?, ?> redisTemplate) {
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.serverCommands().flushAll();
//            return null;
//        });
//    }
//
//    @DynamicPropertySource
//    static void configureRedis(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.redis.host", REDIS::getHost);
//        registry.add("spring.data.redis.port", () -> REDIS.getFirstMappedPort().toString());
//    }
//
//    class RedisContainerHolder {
//
//        private static final RedisContainer INSTANCE = new RedisContainer(DockerImageName.parse("redis:7.0.8-alpine"));
//
//        static {
//            INSTANCE.start();
//        }
//
//        public static RedisContainer getInstance() {
//            return INSTANCE;
//        }
//    }
//}
