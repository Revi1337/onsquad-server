package revi1337.onsquad.infrastructure.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.client.CacheableS3StorageManager;
import revi1337.onsquad.infrastructure.aws.s3.client.S3StorageCleaner;
import revi1337.onsquad.infrastructure.aws.s3.client.S3StorageManager;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Bean
    public CloudFrontCacheInvalidator cloudfrontCacheInvalidator(CloudFrontClient cloudFrontClient, CloudFrontProperties cloudfrontProperties) {
        return new CloudFrontCacheInvalidator(cloudFrontClient, cloudfrontProperties.distributionId());
    }

    @Bean
    public S3StorageManager s3StorageManager(S3Client s3Client, S3BucketProperties s3BucketProperties) {
        return new S3StorageManager(s3Client, s3BucketProperties.bucket());
    }

    @Bean
    public CacheableS3StorageManager cacheableS3StorageManager(
            S3StorageManager s3StorageManager,
            CloudFrontProperties cloudfrontProperties,
            CloudFrontCacheInvalidator cacheInvalidator
    ) {
        return new CacheableS3StorageManager(s3StorageManager, cacheInvalidator, cloudfrontProperties.baseDomain());
    }

    @Bean
    public S3StorageCleaner s3StorageCleaner(S3Client s3Client, S3BucketProperties s3BucketProperties) {
        return new S3StorageCleaner(s3Client, s3BucketProperties.bucket());
    }
}
