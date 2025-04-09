package revi1337.onsquad.inrastructure.file.application;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageManager {

    String upload(byte[] content, String currentFileName);

    String upload(MultipartFile multipartFile);

    String upload(MultipartFile multipartFile, String currentFileName);

    void delete(String uploadUrl);

}
