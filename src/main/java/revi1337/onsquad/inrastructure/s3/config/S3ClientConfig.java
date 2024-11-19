package revi1337.onsquad.inrastructure.s3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Configuration
public class S3ClientConfig {

    private final S3BucketProperties s3BucketProperties;

    @Bean
    public S3Client amazonS3() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3BucketProperties.s3().accessKey(), s3BucketProperties.s3().secretKey()
        );

        return S3Client.builder()
                .region(Region.of(s3BucketProperties.s3().region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
