package revi1337.onsquad.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerSupport {

    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT)
                .toString());
    }
}


//package revi1337.onsquad.support;
//
//import com.redis.testcontainers.RedisContainer;
//import org.springframework.boot.test.util.TestPropertyValues;
//import org.springframework.context.ApplicationContextInitializer;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public abstract class TestContainerSupport {
//
//    static RedisContainer redis;
//
//    static {
//        redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("latest"));
//        redis.start();
//    }
//
//    static class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//        @Override
//        public void initialize(ConfigurableApplicationContext applicationContext) {
//            Map<String, String> properties = new HashMap<>();
//            String redisHost = redis.getHost();
//            Integer redisPort = redis.getFirstMappedPort();
//
//            properties.put("spring.data.redis.host", redisHost);
//            properties.put("spring.data.redis.port", redisPort.toString());
//
//            TestPropertyValues.of(properties)
//                    .applyTo(applicationContext);
//        }
//    }
//
//}
