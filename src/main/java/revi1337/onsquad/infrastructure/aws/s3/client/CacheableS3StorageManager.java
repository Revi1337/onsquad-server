package revi1337.onsquad.infrastructure.aws.s3.client;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;

@RequiredArgsConstructor
public class CacheableS3StorageManager implements FileStorageManager {

    private final S3StorageManager s3StorageManager;
    private final CloudFrontCacheInvalidator cacheInvalidator;
    private final String cloudFrontDomain;

    @Override
    public String upload(byte[] fileBytes, String filePathAndName) {
        String fileUrl = s3StorageManager.upload(fileBytes, filePathAndName);
        String path = URI.create(fileUrl).getPath();

        return cloudFrontDomain.concat(path);
    }

    @Override
    public String upload(MultipartFile multipart) {
        String fileUrl = s3StorageManager.upload(multipart);
        String path = URI.create(fileUrl).getPath();

        return cloudFrontDomain.concat(path);
    }

    @Override
    public String upload(MultipartFile multipart, String filePathAndName) {
        String fileUrl = s3StorageManager.upload(multipart, filePathAndName);
        String path = URI.create(fileUrl).getPath();

        return cloudFrontDomain.concat(path);
    }

    @Override
    public void delete(String filePathAndName) {
        s3StorageManager.delete(filePathAndName);
        cacheInvalidator.createInvalidation(filePathAndName);
    }
}
