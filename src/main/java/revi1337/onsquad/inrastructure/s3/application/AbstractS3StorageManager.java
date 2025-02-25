package revi1337.onsquad.inrastructure.s3.application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.http.MediaType;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import revi1337.onsquad.inrastructure.s3.error.S3ErrorCode;
import revi1337.onsquad.inrastructure.s3.error.exception.S3BusinessException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public abstract class AbstractS3StorageManager implements S3StorageManager {

    protected final S3BucketProperties s3BucketProperties;
    protected final S3Client s3Client;
    protected final String baseBucket;
    protected final String rootDirectory;
    protected final String region;

    public AbstractS3StorageManager(S3BucketProperties s3BucketProperties, S3Client s3Client) {
        this.s3BucketProperties = s3BucketProperties;
        this.s3Client = s3Client;
        this.baseBucket = s3BucketProperties.s3().bucket();
        this.rootDirectory = s3BucketProperties.s3().directory().root();
        this.region = s3BucketProperties.s3().region();
    }

    protected abstract String getTargetDirectoryPath();

    @Override
    public String uploadFile(byte[] fileContent, String uploadUri, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, fileContent.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
            PutObjectRequest uploadRequest = buildUploadRequest(uploadUri, mediaType);
            s3Client.putObject(uploadRequest, requestBody);
            return String.format(S3_BASE_FORMAT, baseBucket, region, uploadUri);
        } catch (IOException | IllegalArgumentException e) {
            throw new S3BusinessException.UploadFail(S3ErrorCode.FAIL_UPLOAD);
        }
    }

    @Override
    public void deleteFile(String uploadUrl) {
    }

    private PutObjectRequest buildUploadRequest(String uriPath, MediaType mediaType) {
        return PutObjectRequest.builder()
                .bucket(baseBucket)
                .key(uriPath)
                .contentType(mediaType.toString())
                .build();
    }
}
