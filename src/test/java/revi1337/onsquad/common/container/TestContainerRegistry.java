package revi1337.onsquad.common.container;

import com.redis.testcontainers.RedisContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(TestContainerRegistry.class);

    private static RedisContainer redis;
    private static MySQLContainer<?> mysql;
    private static LocalStackContainer aws;

    public static RedisContainer getRedis() {
        if (redis == null) {
            synchronized (TestContainerRegistry.class) {
                if (redis == null) {
                    logger.info("Initializing Redis Test Container...");
                    redis = new RedisContainer(DockerImageName.parse("redis:7.0.8-alpine"))
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.redis")));
                    redis.start();
                    logger.info("Redis started: {}:{}", redis.getHost(), redis.getFirstMappedPort());
                }
            }
        }
        return redis;
    }

    public static MySQLContainer<?> getMysql() {
        if (mysql == null) {
            synchronized (TestContainerRegistry.class) {
                if (mysql == null) {
                    logger.info("Initializing MySQL Test Container...");
                    mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                            .withCommand("--lower_case_table_names=1")
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));
                    mysql.start();
                    logger.info("MySQL started");
                }
            }
        }
        return mysql;
    }

    public static LocalStackContainer getAws() {
        if (aws == null) {
            synchronized (TestContainerRegistry.class) {
                if (aws == null) {
                    logger.info("Initializing AWS Test Container...");
                    aws = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.3"))
                            .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.CLOUDFORMATION)
                            .withEnv("AWS_DEFAULT_REGION", "ap-northeast-2")
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.aws")));
                    aws.start();
                    createTestBucket(aws);
                    logger.info("AWS LocalStack started");
                }
            }
        }
        return aws;
    }

    private static void createTestBucket(LocalStackContainer aws) {
        try {
            aws.execInContainer(
                    "awslocal", "s3api", "create-bucket",
                    "--bucket", "onsquad",
                    "--create-bucket-configuration",
                    "LocationConstraint=ap-northeast-2"
            );
            logger.info("Test bucket created");
        } catch (Exception e) {
            logger.error("Failed to create bucket", e);
        }
    }
}
