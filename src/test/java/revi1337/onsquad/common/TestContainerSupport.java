package revi1337.onsquad.common;

import com.redis.testcontainers.RedisContainer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(initializers = TestContainerSupport.IntegrationTestInitializer.class)
public abstract class TestContainerSupport {

    static Logger logger = LoggerFactory.getLogger(TestContainerSupport.class);
    static RedisContainer redis;
    static LocalStackContainer aws;

    static {
        redis = setUpRedisContainer();
        aws = setUpLocalStackContainer();

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
            properties.put("s3.endpoint", "http://" + aws.getHost() + ":" + aws.getMappedPort(4566));
            TestPropertyValues.of(properties).applyTo(applicationContext);

            createTestBucket();
        }
    }

    private static RedisContainer setUpRedisContainer() {
        return new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7.0.8-alpine"))
                .withCreateContainerCmdModifier(cmd -> cmd.withName("onsquad-test-redis"));
    }

    private static LocalStackContainer setUpLocalStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("0.14.3"))
                .withServices(LocalStackContainer.Service.S3)
                .withExposedPorts(4566)
                .withEnv("DEBUG", "1")
                .withEnv("SSL_DISABLE", "1")
                .withEnv("PORT_WEB_UI", "4567")
                .withEnv("AWS_ACCESS_KEY_ID", "access-key")
                .withEnv("AWS_SECRET_ACCESS_KEY", "secret-key")
                .withEnv("AWS_DEFAULT_REGION", "ap-northeast-2")
                .withServices(LocalStackContainer.Service.CLOUDFORMATION)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("onsquad-test-localstack"));
    }

    private static void createTestBucket() {
        try {
            ExecResult execResult = aws.execInContainer("awslocal", "s3api", "create-bucket", "--bucket", "onsquad",
                    "--create-bucket-configuration", "LocationConstraint=ap-northeast-2");
            logger.info("Success Initializing Test Bucket");
            logger.info(execResult.getStdout());
        } catch (Exception e) {
            logger.error("Error while Initializing Test Bucket", e);
        }
    }
}
