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

    public static class NotFound extends AnnounceBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class MismatchReference extends AnnounceBusinessException {

        public MismatchReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InsufficientAuthority extends AnnounceBusinessException {

        public InsufficientAuthority(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
