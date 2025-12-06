package revi1337.onsquad.infrastructure.aws.cloudfront;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "onsquad.aws.cloud-front")
public record CloudFrontProperties(
        String baseDomain,
        String region,
        String distributionId
) {

}
