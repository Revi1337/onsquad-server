package revi1337.onsquad.common.container;

import com.redis.testcontainers.RedisContainer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTestContainerInitializer.class);
    private static final RedisContainer REDIS = new RedisContainer(DockerImageName.parse("redis:7.0.8-alpine"))
            .withReuse(true)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.redis")));

    static {
        LOGGER.info("Initializing Redis Test Container...");
        REDIS.start();
        LOGGER.info("Redis started: {}:{}", REDIS.getHost(), REDIS.getFirstMappedPort());
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Map<String, String> properties = new HashMap<>();
        properties.put("spring.data.redis.host", REDIS.getHost());
        properties.put("spring.data.redis.port", REDIS.getFirstMappedPort().toString());
        TestPropertyValues.of(properties).applyTo(applicationContext);
    }
}
