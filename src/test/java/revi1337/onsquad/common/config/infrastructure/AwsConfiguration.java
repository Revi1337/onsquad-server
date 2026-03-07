package revi1337.onsquad.common.config.infrastructure;

import java.net.URI;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class AwsConfiguration {

    @TestConfiguration
    public static class S3Configuration {

        @Primary
        @Bean("s3Client")
        public S3Client amazonS3(S3BucketProperties s3BucketProperties) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(s3BucketProperties.accessKey(), s3BucketProperties.secretKey());

            return S3Client.builder()
                    .endpointOverride(URI.create(s3BucketProperties.endpoint()))
                    .forcePathStyle(true)
                    .region(Region.of(s3BucketProperties.region()))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        }
    }

    @TestConfiguration
    public static class CloudFrontConfiguration {

        @Primary
        @Bean("cloudFrontClient")
        public CloudFrontClient cloudFrontClient(S3BucketProperties s3BucketProperties, CloudFrontProperties cloudfrontProperties) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(s3BucketProperties.accessKey(), s3BucketProperties.secretKey());

            return CloudFrontClient.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.of(cloudfrontProperties.region()))
                    .build();
        }
    }
}
