package revi1337.onsquad.inrastructure.s3.application;

public interface FileUploadManager {

    String uploadFile(byte[] fileContent, String uploadUri, String fileName);

    void deleteFile(String uploadUrl);

}
