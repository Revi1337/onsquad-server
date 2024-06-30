package revi1337.onsquad.image.application;

import revi1337.onsquad.image.domain.vo.SupportAttachmentType;
import revi1337.onsquad.image.error.exception.AttachmentValidationException;

import java.util.Arrays;

import static revi1337.onsquad.image.error.AttachmentErrorCode.*;

public abstract class AttachmentMagicByteValidator {

    public static void validateMagicByte(byte[] binary) {
        boolean isValid = SupportAttachmentType.defaultEnumSet().stream()
                .anyMatch(support -> {
                    byte[] supportMagicByte = support.getMagicByte();
                    byte[] binaryMagicByte = Arrays.copyOfRange(binary, 0, supportMagicByte.length);
                    return Arrays.equals(supportMagicByte, binaryMagicByte);
                });

        if (!isValid) {
            throw new AttachmentValidationException.UnsupportedAttachmentType(
                    UNSUPPORTED_MAGIC_BYTE,
                    SupportAttachmentType.convertSupportedTypeString()
            );
        }
    }
}
