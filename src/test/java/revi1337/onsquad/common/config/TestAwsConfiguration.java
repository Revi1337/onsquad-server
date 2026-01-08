package revi1337.onsquad.common.config;

import static org.mockito.Mockito.mock;

import java.net.URI;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.common.application.file.UUIDFilenameConverter;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.S3BucketProperties;
import revi1337.onsquad.infrastructure.aws.s3.S3StorageManager;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestAwsConfiguration {

    private final S3BucketProperties s3BucketProperties;
    private final CloudFrontProperties cloudFrontProperties;

    public TestAwsConfiguration(CloudFrontProperties cloudFrontProperties, S3BucketProperties s3BucketProperties) {
        this.cloudFrontProperties = cloudFrontProperties;
        this.s3BucketProperties = s3BucketProperties;
    }

    @Bean
    public S3Client testAmazonS3() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3BucketProperties.accessKey(), s3BucketProperties.secretKey());

        return S3Client.builder()
                .region(Region.of(s3BucketProperties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create("http://localhost:4566"))
                .build();
    }

    @Bean
    public FileStorageManager testCrewS3StorageManager() {
        return new S3StorageManager(
                testAmazonS3(),
                mock(CloudFrontCacheInvalidator.class),
                new UUIDFilenameConverter(),
                s3BucketProperties.bucket(),
                s3BucketProperties.getActualCrewAssets(),
                cloudFrontProperties.baseDomain()
        );
    }

    @Bean
    public FileStorageManager testMemberS3StorageManager() {
        return new S3StorageManager(
                testAmazonS3(),
                mock(CloudFrontCacheInvalidator.class),
                new UUIDFilenameConverter(),
                s3BucketProperties.bucket(),
                s3BucketProperties.getActualMemberAssets(),
                cloudFrontProperties.baseDomain()
        );
    }
}
