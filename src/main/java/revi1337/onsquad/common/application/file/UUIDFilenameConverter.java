package revi1337.onsquad.common.application.file;

import java.util.UUID;
import revi1337.onsquad.common.constant.Sign;

public class UUIDFilenameConverter implements FilenameConvertStrategy {

    @Override
    public String convert(String filename) {
        int delimiterIndex = filename.lastIndexOf(Sign.DOT);
        String extension = (delimiterIndex != -1) ? filename.substring(delimiterIndex) : Sign.WHITESPACE;
        return UUID.randomUUID() + extension;
    }
}
