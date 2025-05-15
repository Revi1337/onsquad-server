package revi1337.onsquad.announce.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class AnnounceBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public AnnounceBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotFoundById extends AnnounceBusinessException {

        public NotFoundById(ErrorCode errorCode, Long announceId) {
            super(errorCode, String.format(errorCode.getDescription(), announceId));
        }
    }

    public static class InvalidReference extends AnnounceBusinessException {

        public InvalidReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
