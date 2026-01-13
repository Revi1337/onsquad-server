package revi1337.onsquad.infrastructure.aws.s3.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.common.application.file.FilenameConvertStrategy;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.aws.s3.error.FileActionException;
import revi1337.onsquad.infrastructure.aws.s3.error.FileErrorCode;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@RequiredArgsConstructor
public class S3StorageManager implements FileStorageManager {

    private final S3Client s3Client;
    private final CloudFrontCacheInvalidator cacheInvalidator;
    private final FilenameConvertStrategy convertStrategy;
    private final String bucket;
    private final String assetsDir;
    private final String cloudFrontDomain;

    @Override
    public String upload(byte[] fileContent, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, fileContent.length);
            MediaType type = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
            String uuidFileName = convertStrategy.convert(fileName);

            return uploadFile(uuidFileName, type, requestBody, false);
        } catch (IOException exception) {
            log.error("byte array 처리 중 예외 발생", exception);
            throw new FileActionException.ProcessFail(FileErrorCode.FAIL_PROCESS, exception);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
            MediaType type = MediaType.parseMediaType(Files.probeContentType(Paths.get(file.getOriginalFilename())));
            String uuidFileName = convertStrategy.convert(file.getOriginalFilename());

            return uploadFile(uuidFileName, type, requestBody, false);
        } catch (IOException exception) {
            log.error("multipart 처리 중 예외 발생", exception);
            throw new FileActionException.ProcessFail(FileErrorCode.FAIL_PROCESS, exception);
        }
    }

    @Override
    public String upload(MultipartFile file, String targetFileName) {
        try {
            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
            MediaType type = MediaType.parseMediaType(Files.probeContentType(Paths.get(file.getOriginalFilename())));
            String fileName = Paths.get(URI.create(targetFileName).getPath()).getFileName().toString();

            return uploadFile(fileName, type, requestBody, true);
        } catch (IOException exception) {
            log.error("multipart 처리 중 예외 발생", exception);
            throw new FileActionException.ProcessFail(FileErrorCode.FAIL_PROCESS, exception);
        }
    }

    @Override
    public void delete(String remoteUrl) {
        try {
            String removePath = remoteUrl.replaceFirst(cloudFrontDomain, Sign.EMPTY).substring(1);
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(removePath)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (SdkClientException | AwsServiceException exception) {
            log.error("s3 삭제 중 예외 발생", exception);
            throw new FileActionException.DeleteFail(FileErrorCode.FAIL_DELETE, exception);
        }
    }

    private String uploadFile(String fileName, MediaType mediaType, RequestBody requestBody, boolean invalidateCache) {
        try {
            String uploadPath = String.join(Sign.SLASH, assetsDir, fileName);
            PutObjectRequest uploadRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uploadPath)
                    .contentType(mediaType.toString())
                    .build();

            s3Client.putObject(uploadRequest, requestBody);
            if (invalidateCache) {
                cacheInvalidator.createInvalidation(Sign.SLASH + uploadPath);
            }

            return String.join(Sign.SLASH, cloudFrontDomain, uploadPath);
        } catch (SdkClientException | AwsServiceException exception) {
            log.error("s3 업로드 중 예외 발생", exception);
            throw new FileActionException.UploadFail(FileErrorCode.FAIL_UPLOAD, exception);
        }
    }
}
