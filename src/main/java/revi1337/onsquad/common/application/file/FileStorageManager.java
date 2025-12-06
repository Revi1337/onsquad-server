package revi1337.onsquad.common.application.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageManager {

    String upload(byte[] content, String currentFileName);

    String upload(MultipartFile multipartFile);

    String upload(MultipartFile multipartFile, String currentFileName);

    void delete(String uploadUrl);

}
