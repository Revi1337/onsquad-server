package revi1337.onsquad.inrastructure.s3.application;

public interface FileUploadManager {

    String PATH_DELIMITER = "/";
    String FILE_EXTENSION_DELIMITER = ".";

    String uploadFile(byte[] imageData, String imageName);

    String updateFile(String uploadUrl, byte[] imageData, String imageName);

    void deleteFile(String uploadUrl);

}
