package revi1337.onsquad.inrastructure.file.application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import revi1337.onsquad.common.constant.Sign;

public class FileUtils {

    public static final String FILE_EXTENSION_DELIMITER = Sign.DOT;
    public static final String PATH_DELIMITER = Sign.SLASH;

    public static String convertToUUID(String fileName) {
        int delimiterIndex = fileName.lastIndexOf(FILE_EXTENSION_DELIMITER);
        String extension = (delimiterIndex != -1) ? fileName.substring(delimiterIndex) : Sign.WHITESPACE;
        return UUID.randomUUID() + extension;
    }

    public static String parseUriFromUrl(String remoteAddress) {
        try {
            return new URL(remoteAddress).getPath().substring(1);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
    }
}
