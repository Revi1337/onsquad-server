package revi1337.onsquad.inrastructure.file.config.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.inrastructure.file.application.FileStorageManager;
import revi1337.onsquad.inrastructure.file.application.UUIDFilenameConverter;
import revi1337.onsquad.inrastructure.file.application.s3.CloudFrontCacheInvalidator;
import revi1337.onsquad.inrastructure.file.application.s3.S3StorageCleaner;
import revi1337.onsquad.inrastructure.file.application.s3.S3StorageManager;
import revi1337.onsquad.inrastructure.file.config.s3.properties.CloudFrontProperties;
import revi1337.onsquad.inrastructure.file.config.s3.properties.S3BucketProperties;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Configuration
public class S3ApplicationConfig {

    private final S3BucketProperties s3BucketProperties;
    private final CloudFrontProperties cloudFrontProperties;
    private final CloudFrontCacheInvalidator cacheInvalidator;
    private final S3Client s3Client;

    @Bean
    public S3StorageCleaner s3StorageCleaner() {
        return new S3StorageCleaner(s3Client, s3BucketProperties.bucket(), cloudFrontProperties.baseDomain());
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
