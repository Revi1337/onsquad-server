package revi1337.onsquad.infrastructure.aws.s3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Bean("s3Client")
    public S3Client amazonS3(S3BucketProperties s3BucketProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3BucketProperties.accessKey(), s3BucketProperties.secretKey());

        return S3Client.builder()
                .region(Region.of(s3BucketProperties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
