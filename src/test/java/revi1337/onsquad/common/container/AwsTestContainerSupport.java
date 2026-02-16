package revi1337.onsquad.common.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public interface AwsTestContainerSupport {

    LocalStackContainer AWS = AwsContainerHolder.getInstance();

    @DynamicPropertySource
    static void configureAws(DynamicPropertyRegistry registry) {
        registry.add("s3.endpoint", () -> "http://" + AWS.getHost() + ":" + AWS.getMappedPort(4566));
    }

    class AwsContainerHolder {

        private static final LocalStackContainer INSTANCE = new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("0.14.3"))
                .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.CLOUDFORMATION)
                .withEnv("AWS_DEFAULT_REGION", "ap-northeast-2");

        static {
            INSTANCE.start();
            initializeS3Bucket();
        }

        private static void initializeS3Bucket() {
            try {
                INSTANCE.execInContainer("awslocal", "s3api", "create-bucket", "--bucket", "onsquad");
            } catch (Exception e) {
                throw new RuntimeException("S3 Bucket Initialized Fail", e);
            }
        }

        public static LocalStackContainer getInstance() {
            return INSTANCE;
        }
    }
}
