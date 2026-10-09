package revi1337.onsquad.infrastructure.validation.filesystem;

import static revi1337.onsquad.infrastructure.validation.filesystem.error.MagicByteErrorCode.UNSUPPORTED_MAGIC_BYTE;

import java.io.IOException;
import java.io.InputStream;
import revi1337.onsquad.common.constant.SupportMediaType;
import revi1337.onsquad.common.error.FileActionException;
import revi1337.onsquad.common.error.FileErrorCode;
import revi1337.onsquad.infrastructure.validation.filesystem.error.MagicByteValidationException.UnsupportedMagicByteType;

@Deprecated
public abstract class MagicByteValidator {

    public static void validateMagicByte(byte[] binary) {
        if (!SupportMediaType.matchesAny(binary)) {
            throw new UnsupportedMagicByteType(UNSUPPORTED_MAGIC_BYTE, SupportMediaType.convertSupportedTypeString());
        }
    }

    public static void validateMagicByte(InputStream inputStream) {
        try {
            validateMagicByte(inputStream.readNBytes(SupportMediaType.MAX_SIGNATURE_LENGTH));
        } catch (IOException exception) {
            throw new FileActionException.ProcessFail(FileErrorCode.FAIL_PROCESS, exception);
        }
    }
}
