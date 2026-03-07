package revi1337.onsquad.common.application.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.common.util.FileNamingUtils;

@RequiredArgsConstructor
public abstract class AbstractFileStorageManager {

    private final FileStorageManager fileStorageManager;
    private final String directory;

    public String upload(byte[] fileBytes, String fileNameAndPath) {
        String filePath = String.join(Sign.SLASH, directory, FileNamingUtils.createUuidName(fileNameAndPath));
        return fileStorageManager.upload(fileBytes, filePath);
    }

    public String upload(MultipartFile multipart) {
        String filePath = String.join(Sign.SLASH, directory, FileNamingUtils.createUuidName(multipart.getOriginalFilename()));
        return fileStorageManager.upload(multipart, filePath);
    }

    public String upload(MultipartFile multipart, String fileNameAndPath) {
        String filePath = String.join(Sign.SLASH, directory, FileNamingUtils.createUuidName(fileNameAndPath));
        return fileStorageManager.upload(multipart, filePath);
    }

    public void delete(String fileNameAndPath) {
        fileStorageManager.delete(fileNameAndPath);
    }
}
