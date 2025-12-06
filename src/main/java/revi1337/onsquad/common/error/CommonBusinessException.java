package revi1337.onsquad.common.error;

import lombok.Getter;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

@Getter
public abstract class CommonBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CommonBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class RequestConflict extends AnnounceBusinessException {

        public RequestConflict(ErrorCode errorCode, String timeStr) {
            super(errorCode, String.format(errorCode.getDescription(), timeStr));
        }
    }
}
