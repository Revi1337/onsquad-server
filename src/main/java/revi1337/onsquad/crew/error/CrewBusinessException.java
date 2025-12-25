package revi1337.onsquad.crew.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CrewBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CrewBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotFound extends CrewBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class AlreadyExists extends CrewBusinessException {

        public AlreadyExists(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class InsufficientAuthority extends CrewBusinessException {

        public InsufficientAuthority(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }
}
