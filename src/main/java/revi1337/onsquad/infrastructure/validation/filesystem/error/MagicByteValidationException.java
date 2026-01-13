package revi1337.onsquad.infrastructure.validation.filesystem.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Deprecated
@Getter
public abstract class MagicByteValidationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public MagicByteValidationException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class UnsupportedMagicByteType extends MagicByteValidationException {

        public UnsupportedMagicByteType(ErrorCode errorCode, String supportedTypes) {
            super(errorCode, String.format(errorCode.getDescription(), supportedTypes));
        }
    }
}
