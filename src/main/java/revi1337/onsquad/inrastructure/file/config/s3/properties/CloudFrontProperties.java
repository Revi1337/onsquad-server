package revi1337.onsquad.inrastructure.file.config.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "onsquad.aws.cloud-front")
public record CloudFrontProperties(
        String baseDomain,
        String region,
        String distributionId
) {
}
