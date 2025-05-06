package revi1337.onsquad.announce.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class AnnounceDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public AnnounceDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidLength extends AnnounceDomainException {

        public InvalidLength(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
