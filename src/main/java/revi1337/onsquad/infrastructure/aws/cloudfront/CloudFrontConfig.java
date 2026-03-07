package revi1337.onsquad.infrastructure.aws.cloudfront;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

@Configuration
public class CloudFrontConfig {

    @Bean("cloudFrontClient")
    public CloudFrontClient cloudFrontClient(S3BucketProperties s3BucketProperties, CloudFrontProperties cloudfrontProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3BucketProperties.accessKey(), s3BucketProperties.secretKey());

        return CloudFrontClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(cloudfrontProperties.region()))
                .build();
    }
}
