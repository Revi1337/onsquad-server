package revi1337.onsquad.inrastructure.file.support;

import static revi1337.onsquad.inrastructure.file.error.AttachmentErrorCode.UNSUPPORTED_MAGIC_BYTE;

import revi1337.onsquad.inrastructure.file.application.SupportAttachmentType;
import revi1337.onsquad.inrastructure.file.error.exception.AttachmentValidationException;

public abstract class AttachmentMagicByteValidator {

    public static void validateMagicByte(byte[] binary) {
        boolean isValid = SupportAttachmentType.defaultEnumSet().stream()
                .anyMatch(support -> support.matches(binary));

        if (!isValid) {
            throw new AttachmentValidationException.UnsupportedAttachmentType(
                    UNSUPPORTED_MAGIC_BYTE,
                    SupportAttachmentType.convertSupportedTypeString()
            );
        }
    }
}
