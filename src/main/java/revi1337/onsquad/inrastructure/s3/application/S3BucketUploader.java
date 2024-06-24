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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3BucketUploader {

    private static final String PATH_DELIMITER = "/";
    private static final String S3_LINK_FORMAT = "https://%s.s3.%s.amazonaws.com/%s"; // https://{bucketName}.s3.{region}.amazonaws.com/{path}

    private final S3BucketProperties s3BucketProperties;
    private final S3Client s3Client;

    public String uploadCrew(byte[] content, String originalFileName) { // TODO 확장할 여지가 있을 때 객체지향적으로 해결하여 OCP 를 지켜야 한다.ㄴ
        Directories directories = s3BucketProperties.s3().directory().directories();
        return uploadImage(directories.crewDirectory(), content, originalFileName);
    }

    public String uploadSquad(byte[] content, String originalFileName) { // TODO 확장할 여지가 있을 때 객체지향적으로 해결하여 OCP 를 지켜야 한다.
        Directories directories = s3BucketProperties.s3().directory().directories();
        return uploadImage(directories.squadDirectory(), content, originalFileName);
    }

    public String uploadImage(String directoryPath, byte[] content, String originalFileName) {
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, content.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(originalFileName)));
            return uploadFile(directoryPath, originalFileName, requestBody, mediaType);
        } catch (IOException e) {
            log.error("{} s3 업로드 실패", directoryPath, e);
            throw new IllegalArgumentException("byte array s3업로드 예외", e); // TODO 커스텀 익셉션 필요
        }
    }

    private String uploadFile(String directoryPath, String originalFileName, RequestBody requestBody, MediaType mediaType) {
        S3 s3 = s3BucketProperties.s3();
        String rootDirectory = s3.directory().root();
        String fileNameWithFullPath = String.join(PATH_DELIMITER, buildPath(rootDirectory, directoryPath, originalFileName));
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .key(fileNameWithFullPath)
                .contentType(mediaType.toString())
                .bucket(s3.bucket())
                .build();

        s3Client.putObject(putObjectRequest, requestBody);

        String uploadRemoteAddress = String.format(S3_LINK_FORMAT, s3.bucket(), s3.region(), fileNameWithFullPath);
        log.info("file uploaded : {}", uploadRemoteAddress);
        return uploadRemoteAddress;
    }

    private List<String> buildPath(String... paths) {
        return new ArrayList<>() {
            {
                addAll(Arrays.asList(paths));
            }
        };
    }
}
