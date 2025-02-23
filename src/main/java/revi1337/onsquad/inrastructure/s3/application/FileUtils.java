package revi1337.onsquad.inrastructure.s3.application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class FileUtils {

    public static final String FILE_EXTENSION_DELIMITER = ".";
    public static final String PATH_DELIMITER = "/";

    public String convertToUUID(String fileName) {
        int delimiterIndex = fileName.lastIndexOf(FILE_EXTENSION_DELIMITER);
        String extension = (delimiterIndex != -1) ? fileName.substring(delimiterIndex) : "";
        return UUID.randomUUID() + extension;
    }

    public String parseUriFromUrl(String remoteAddress) {
        try {
            return new URL(remoteAddress).getPath().substring(1);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
    }
}
