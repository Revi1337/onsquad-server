package revi1337.onsquad.infrastructure.validation.filesystem;

import static revi1337.onsquad.infrastructure.validation.filesystem.error.MagicByteErrorCode.UNSUPPORTED_MAGIC_BYTE;

import revi1337.onsquad.common.constant.SupportMediaType;
import revi1337.onsquad.infrastructure.validation.filesystem.error.MagicByteValidationException.UnsupportedMagicByteType;

@Deprecated
public abstract class MagicByteValidator {

    public static void validateMagicByte(byte[] binary) {
        boolean isValid = SupportMediaType.defaultEnumSet().stream()
                .anyMatch(support -> support.matches(binary));

        if (!isValid) {
            throw new UnsupportedMagicByteType(
                    UNSUPPORTED_MAGIC_BYTE,
                    SupportMediaType.convertSupportedTypeString()
            );
        }
    }
}
