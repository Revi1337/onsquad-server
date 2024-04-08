package revi1337.onsquad.support;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

public abstract class TestContainerSupport {

    static RedisContainer redis;

    static {
        redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("latest"));
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
