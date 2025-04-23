package revi1337.onsquad.common;

import com.redis.testcontainers.RedisContainer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(initializers = TestContainerSupport.IntegrationTestInitializer.class)
public abstract class TestContainerSupport {

    static RedisContainer redis;
    static LocalStackContainer aws;

    static {
        redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7.0.8-alpine"));
        aws = new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.2"))
                .withServices(LocalStackContainer.Service.S3)
                .withStartupTimeout(Duration.ofSeconds(600));

        redis.start();
        aws.start();
    }

    static class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();
            String redisHost = redis.getHost();
            Integer redisPort = redis.getFirstMappedPort();
            properties.put("spring.data.redis.host", redisHost);
            properties.put("spring.data.redis.port", redisPort.toString());
            createTestBucket();
            TestPropertyValues.of(properties).applyTo(applicationContext);
        }

        private static void createTestBucket() {
            try {
                aws.execInContainer("awslocal", "s3api", "create-bucket", "--bucket", "onsqaud");
            } catch (Exception e) {
            }
        }
    }
}
