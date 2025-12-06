package revi1337.onsquad.infrastructure.aws.s3.support;

import static revi1337.onsquad.infrastructure.aws.s3.error.AttachmentErrorCode.UNSUPPORTED_MAGIC_BYTE;

import revi1337.onsquad.common.constant.SupportMediaType;
import revi1337.onsquad.infrastructure.aws.s3.error.AttachmentValidationException;

@Deprecated
public abstract class AttachmentMagicByteValidator {

    public static void validateMagicByte(byte[] binary) {
        boolean isValid = SupportMediaType.defaultEnumSet().stream()
                .anyMatch(support -> support.matches(binary));

        if (!isValid) {
            throw new AttachmentValidationException.UnsupportedAttachmentType(
                    UNSUPPORTED_MAGIC_BYTE,
                    SupportMediaType.convertSupportedTypeString()
            );
        }
    }
}
