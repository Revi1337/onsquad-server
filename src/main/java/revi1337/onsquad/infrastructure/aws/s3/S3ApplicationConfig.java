package revi1337.onsquad.infrastructure.aws.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.support.UUIDFilenameConverter;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Configuration
public class S3ApplicationConfig {

    private final S3BucketProperties s3BucketProperties;
    private final CloudFrontCacheInvalidator cacheInvalidator;
    private final CloudFrontProperties cloudFrontProperties;
    private final S3Client s3Client;

    @Bean
    public S3StorageCleaner s3StorageCleaner() {
        return new S3StorageCleaner(s3Client, s3BucketProperties.bucket());
    }

    @Bean
    public FileStorageManager crewS3StorageManager() {
        return new S3StorageManager(
                s3Client,
                cacheInvalidator,
                new UUIDFilenameConverter(),
                s3BucketProperties.bucket(),
                s3BucketProperties.getActualCrewAssets(),
                cloudFrontProperties.baseDomain()
        );
    }

    @Bean
    public FileStorageManager memberS3StorageManager() {
        return new S3StorageManager(
                s3Client,
                cacheInvalidator,
                new UUIDFilenameConverter(),
                s3BucketProperties.bucket(),
                s3BucketProperties.getActualMemberAssets(),
                cloudFrontProperties.baseDomain()
        );
    }
}
