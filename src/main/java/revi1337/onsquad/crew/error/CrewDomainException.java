package revi1337.onsquad.crew.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CrewDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CrewDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidDetailLength extends CrewDomainException {

        public InvalidDetailLength(ErrorCode errorCode, Number beginTo, Number endTo) {
            super(errorCode, String.format(errorCode.getDescription(), beginTo, endTo));
        }
    }

    public static class InvalidHashTagsSize extends CrewDomainException {

        public InvalidHashTagsSize(ErrorCode errorCode, Number maxSize) {
            super(errorCode, String.format(errorCode.getDescription(), maxSize));
        }
    }

    public static class InvalidIntroduceLength extends CrewDomainException {

        public InvalidIntroduceLength(ErrorCode errorCode, Number beginTo, Number endTo) {
            super(errorCode, String.format(errorCode.getDescription(), beginTo, endTo));
        }
    }

    public static class InvalidNameLength extends CrewDomainException {

        public InvalidNameLength(ErrorCode errorCode, Number beginTo, Number endTo) {
            super(errorCode, String.format(errorCode.getDescription(), beginTo, endTo));
        }
    }
}
