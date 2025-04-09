package revi1337.onsquad.inrastructure.file.config.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("onsquad.aws")
public record AwsProperties(
        @NestedConfigurationProperty CloudFrontProperties cloudFront,
        @NestedConfigurationProperty S3BucketProperties s3
) {
}
