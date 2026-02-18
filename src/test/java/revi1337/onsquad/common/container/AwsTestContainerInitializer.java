package revi1337.onsquad.common.container;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public class AwsTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsTestContainerInitializer.class);
    private static final LocalStackContainer AWS = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.3"))
            .withExposedPorts(4566)
            .withEnv("DEBUG", "1")
            .withEnv("SSL_DISABLE", "1")
            .withEnv("PORT_WEB_UI", "4567")
            .withEnv("AWS_ACCESS_KEY_ID", "access-key")
            .withEnv("AWS_SECRET_ACCESS_KEY", "secret-key")
            .withEnv("AWS_DEFAULT_REGION", "ap-northeast-2")
            .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.CLOUDFORMATION)
            .withReuse(true)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.aws")));

    static {
        LOGGER.info("Initializing AWS Test Container...");
        AWS.start();
        createTestBucket(AWS);
        LOGGER.info("AWS LocalStack started");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Map<String, String> properties = new HashMap<>();
        properties.put("s3.endpoint", "http://" + AWS.getHost() + ":" + AWS.getMappedPort(4566));
        TestPropertyValues.of(properties).applyTo(applicationContext);
    }

    private static void createTestBucket(LocalStackContainer aws) {
        try {
            aws.execInContainer(
                    "awslocal", "s3api", "create-bucket",
                    "--bucket", "onsquad",
                    "--create-bucket-configuration",
                    "LocationConstraint=ap-northeast-2"
            );
            LOGGER.info("Test bucket created");
        } catch (Exception e) {
            LOGGER.error("Failed to create bucket", e);
        }
    }
}
