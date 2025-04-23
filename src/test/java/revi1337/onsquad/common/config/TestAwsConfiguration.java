package revi1337.onsquad.common.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import revi1337.onsquad.inrastructure.file.config.s3.properties.S3BucketProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestAwsConfiguration {

    @Value("${onsquad.aws.s3.localstack}")
    private String localStackEndpoint;

    @Bean
    public S3Client amazonS3(S3BucketProperties s3BucketProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3BucketProperties.accessKey(), s3BucketProperties.secretKey()
        );

        return S3Client.builder()
                .region(Region.of(s3BucketProperties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(localStackEndpoint))
                .build();
    }
}
