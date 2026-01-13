package revi1337.onsquad.infrastructure.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.client.S3BucketProperties;

@ConfigurationProperties("onsquad.aws")
public record AwsProperties(
        @NestedConfigurationProperty CloudFrontProperties cloudFront,
        @NestedConfigurationProperty S3BucketProperties s3
) {

}
