package revi1337.onsquad.common.application.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageManager {

    String upload(byte[] fileBytes, String filePathAndName);

    String upload(MultipartFile multipart);

    String upload(MultipartFile multipart, String filePathAndName);

    void delete(String filePathAndName);

}
