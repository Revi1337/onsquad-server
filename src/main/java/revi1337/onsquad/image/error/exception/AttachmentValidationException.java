package revi1337.onsquad.image.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class AttachmentValidationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public AttachmentValidationException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class UnsupportedAttachmentType extends AttachmentValidationException {

        public UnsupportedAttachmentType(ErrorCode errorCode, String supportedTypes) {
            super(errorCode, String.format(errorCode.getDescription(), supportedTypes));
        }
    }
}
