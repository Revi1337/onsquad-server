package revi1337.onsquad.inrastructure.s3.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3BucketUploader {

    private final S3BucketProperties s3BucketProperties;
    private final S3Client s3Client;

    public String uploadCrew(byte[] content, String originalFileName) {
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            Directories directories = s3BucketProperties.s3().directory().directories();
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, content.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(originalFileName)));
            return uploadFile(directories.crewDirectory(), requestBody, mediaType);
        } catch (IOException e) {
            log.error("byte array s3업로드 예외", e);
            throw new IllegalArgumentException("byte array s3업로드 예외", e); // TODO 커스텀 익셉션 필요
        }
    }

    public String uploadSquad(byte[] content, String originalFileName) {
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            Directories directories = s3BucketProperties.s3().directory().directories();
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, content.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(originalFileName)));
            return uploadFile(directories.squadDirectory(), requestBody, mediaType);
        } catch (IOException e) {
            log.error("byte array s3업로드 예외", e);
            throw new IllegalArgumentException("byte array s3업로드 예외", e); // TODO 커스텀 익셉션 필요
        }
    }

    private String uploadFile(String directoryPath, RequestBody requestBody, MediaType mediaType) {
        S3 s3 = s3BucketProperties.s3();
        String rootDirectory = s3.directory().root();
        String uploadPath = rootDirectory + directoryPath;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .key(uploadPath)
                .contentType(mediaType.toString())
                .bucket(s3.bucket())
                .build();

        s3Client.putObject(putObjectRequest, requestBody);

        String uploadedUrl = s3.baseDomain() + directoryPath;
        log.info("file uploaded : {} , published : {}", uploadedUrl, uploadedUrl);
        return uploadedUrl;
    }
}
