package revi1337.onsquad.inrastructure.s3.application;

import javax.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface S3StorageManager extends FileUploadManager {

    /**
     * https://{bucketName}.s3.{region}.amazonaws.com/{path}
     */
    String S3_BASE_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";

    String uploadFile(MultipartFile multipartFile);

    String updateFile(@Nullable String uploadUrl, byte[] fileContent, String fileName);

}
