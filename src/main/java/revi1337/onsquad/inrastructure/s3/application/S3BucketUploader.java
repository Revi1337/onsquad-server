package revi1337.onsquad.inrastructure.s3.application;

import static revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties.Directories;
import static revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties.S3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import revi1337.onsquad.image.application.AttachmentMagicByteValidator;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

// TODO 리팩토링 필요. 해당 클래스 코드 너무 개판.
@Slf4j
@RequiredArgsConstructor
@Component
public class S3BucketUploader {

    private static final String PATH_DELIMITER = "/";
    private static final String FILE_EXTENSION_DELIMITER = ".";
    private static final String S3_LINK_FORMAT = "https://%s.s3.%s.amazonaws.com/%s"; // https://{bucketName}.s3.{region}.amazonaws.com/{path}

    private final S3BucketProperties s3BucketProperties;
    private final S3Client s3Client;

    public String uploadCrew(byte[] content, String fileName) { // TODO 확장할 여지가 있을 때 객체지향적으로 해결하여 OCP 를 지켜야 한다.
        Directories directories = s3BucketProperties.s3().directory().directories();
        return uploadImage(directories.crewDirectory(), content, fileName);
    }

    public String uploadSquad(byte[] content, String fileName) { // TODO 확장할 여지가 있을 때 객체지향적으로 해결하여 OCP 를 지켜야 한다.
        Directories directories = s3BucketProperties.s3().directory().directories();
        return uploadImage(directories.squadDirectory(), content, fileName);
    }

    public String uploadImage(String directoryPath, byte[] content, String fileName) {
        AttachmentMagicByteValidator.validateMagicByte(content);
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, content.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
            return uploadFile(directoryPath, fileName, requestBody, mediaType);
        } catch (IOException e) {
            log.error("{} s3 업로드 실패", directoryPath, e);
            throw new IllegalArgumentException("byte array s3업로드 예외", e);
        }
    }

    private String uploadFile(String directoryPath, String fileName, RequestBody requestBody, MediaType mediaType) {
        S3 s3 = s3BucketProperties.s3();
        String rootDirectory = s3.directory().root();
        String randomFileName = generateUuidFileName(fileName);
        String fileNameWithFullPath = String.join(PATH_DELIMITER, rootDirectory, directoryPath, randomFileName);
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

    public void updateImage(String remoteAddress, byte[] imageData, String imageName) {
        AttachmentMagicByteValidator.validateMagicByte(imageData);
        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
            S3 s3 = s3BucketProperties.s3();
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, imageData.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(imageName)));
            String uri = parseUriFromUrl(remoteAddress);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .key(uri)
                    .contentType(mediaType.toString())
                    .bucket(s3.bucket())
                    .build();

            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException e) {
            throw new IllegalArgumentException("byte array s3업로드 예외", e);
        }
    }

    private String generateUuidFileName(String fileName) {
        int delimeterIndex = fileName.lastIndexOf(FILE_EXTENSION_DELIMITER) + 1;
        String extension = fileName.substring(delimeterIndex);
        return UUID.randomUUID() + FILE_EXTENSION_DELIMITER + extension;
    }

    private String parseUriFromUrl(String remoteAddress) {
        try {
            URL url = new URL(remoteAddress);
            String path = url.getPath();
            if (path != null && path.length() > 1) {
                return path.substring(1);
            }
            return path;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
    }
}
