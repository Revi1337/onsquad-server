package revi1337.onsquad.inrastructure.s3.application;

import java.io.IOException;
import javax.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.image.application.AttachmentMagicByteValidator;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import software.amazon.awssdk.services.s3.S3Client;

// TODO 중복 코드 리팩토링 필요.
@Component
public class CrewS3StorageManager extends AbstractS3StorageManager {

    private final FileUtils fileUtils = new FileUtils();

    public CrewS3StorageManager(S3BucketProperties s3BucketProperties, S3Client s3Client) {
        super(s3BucketProperties, s3Client);
    }

    @Override
    protected String getTargetDirectoryPath() {
        return s3BucketProperties.s3().directory().directories().crewDirectory();
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        try {
            byte[] fileBytes = multipartFile.getBytes();
            AttachmentMagicByteValidator.validateMagicByte(fileBytes);
            String uploadDir = getTargetDirectoryPath();
            String uuidImageName = fileUtils.convertToUUID(multipartFile.getOriginalFilename());
            String uploadUri = String.join(FileUtils.PATH_DELIMITER, rootDirectory, uploadDir, uuidImageName);
            return uploadFile(fileBytes, uploadUri, uuidImageName);
        } catch (IOException e) {
            throw new IllegalArgumentException("Multipart 처리 중 예외 발생", e);
        }
    }

    @Override
    public String updateFile(@Nullable String uploadUrl, byte[] fileContent, String fileName) {
        if (!StringUtils.hasText(uploadUrl)) {
            AttachmentMagicByteValidator.validateMagicByte(fileContent);
            String uploadDir = getTargetDirectoryPath();
            String uuidImageName = fileUtils.convertToUUID(fileName);
            String uploadUri = String.join(FileUtils.PATH_DELIMITER, rootDirectory, uploadDir, uuidImageName);
            return uploadFile(fileContent, uploadUri, uuidImageName);
        }

        AttachmentMagicByteValidator.validateMagicByte(fileContent);
        String uuidImageName = fileUtils.convertToUUID(fileName);
        String uploadUri = fileUtils.parseUriFromUrl(uploadUrl);
        return uploadFile(fileContent, uploadUri, uuidImageName);
    }
}
