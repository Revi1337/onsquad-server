package revi1337.onsquad.crew_request.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CrewRequestBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CrewRequestBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotFound extends CrewRequestBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InvalidReference extends CrewRequestBusinessException {

        public InvalidReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class MismatchReference extends CrewRequestBusinessException {

        public MismatchReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
