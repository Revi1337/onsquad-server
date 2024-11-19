package revi1337.onsquad.inrastructure.s3.application;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import revi1337.onsquad.image.application.AttachmentMagicByteValidator;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
public abstract class DefaultS3FileManager implements FileUploadManager, InitializingBean {

    private static final String S3_LINK_FORMAT = "https://%s.s3.%s.amazonaws.com/%s"; // https://{bucketName}.s3.{region}.amazonaws.com/{path}

    private final S3Client s3Client;
    private final S3BucketProperties s3BucketProperties;

    private String baseBucket;
    private String rootDirectory;
    private String region;

    @Override
    public void afterPropertiesSet() {
        this.baseBucket = s3BucketProperties.s3().bucket();
        this.rootDirectory = s3BucketProperties.s3().directory().root();
        this.region = s3BucketProperties.s3().region();
    }

    @Override
    public String uploadFile(byte[] imageData, String imageName) {
        String uriPath = buildUriPath(imageName);
        return uploadFile(imageData, imageName, uriPath);
    }

    @Override
    public String updateFile(String uploadUrl, byte[] imageData, String imageName) {
        String uriPath = parseUriFromUrl(uploadUrl);
        return uploadFile(imageData, imageName, uriPath);
    }

    @Override
    public void deleteFile(String uploadUrl) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(baseBucket)
                .key(parseUriFromUrl(uploadUrl))
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String uploadFile(byte[] imageData, String imageName, String uriPath) {
        AttachmentMagicByteValidator.validateMagicByte(imageData);
        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, imageData.length);
            MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(imageName)));
            PutObjectRequest uploadRequest = buildUploadRequest(uriPath, mediaType);
            s3Client.putObject(uploadRequest, requestBody);
            return String.format(S3_LINK_FORMAT, baseBucket, region, uriPath);
        } catch (Exception e) {
            throw new IllegalArgumentException("byte array s3 업로드 예외", e);
        }
    }

    private String buildUriPath(String imageName) {
        String uploadDir = getUploadDir();
        String uuidImageName = generateUuidFileName(imageName);
        return String.join(PATH_DELIMITER, rootDirectory, uploadDir, uuidImageName);
    }

    abstract public String getUploadDir();

    private PutObjectRequest buildUploadRequest(String uriPath, MediaType mediaType) {
        return PutObjectRequest.builder()
                .bucket(baseBucket)
                .key(uriPath)
                .contentType(mediaType.toString())
                .build();
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
