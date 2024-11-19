package revi1337.onsquad.support;

import com.redis.testcontainers.RedisContainer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = TestContainerSupport.IntegrationTestInitializer.class)
public abstract class TestContainerSupport {

    static RedisContainer redis;

    static {
        redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7.0.8-alpine"));
        redis.start();
    }

    static class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();

            String redisHost = redis.getHost();
            Integer redisPort = redis.getFirstMappedPort();

            properties.put("spring.data.redis.host", redisHost);
            properties.put("spring.data.redis.port", redisPort.toString());

            TestPropertyValues.of(properties)
                    .applyTo(applicationContext);
        }
    }
}
