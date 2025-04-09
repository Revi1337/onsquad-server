package revi1337.onsquad.inrastructure.file.config.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.inrastructure.file.application.s3.CloudFrontCacheInvalidator;
import revi1337.onsquad.inrastructure.file.config.s3.properties.CloudFrontProperties;
import revi1337.onsquad.inrastructure.file.config.s3.properties.S3BucketProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Configuration
public class S3ClientConfig {

    @Bean
    public S3Client amazonS3(S3BucketProperties s3BucketProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3BucketProperties.accessKey(), s3BucketProperties.secretKey()
        );

        return S3Client.builder()
                .region(Region.of(s3BucketProperties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public CloudFrontCacheInvalidator cloudfrontCacheInvalidator(S3BucketProperties s3BucketProperties,
                                                                 CloudFrontProperties cloudfrontProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3BucketProperties.accessKey(), s3BucketProperties.secretKey()
        );

        return new CloudFrontCacheInvalidator(
                CloudFrontClient.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .region(Region.of(cloudfrontProperties.region()))
                        .build(),
                cloudfrontProperties.distributionId()
        );
    }
}
