package revi1337.onsquad.squad.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidTitleLength extends SquadDomainException {

        public InvalidTitleLength(ErrorCode errorCode, Number minSize, Number maxSize) {
            super(errorCode, String.format(errorCode.getDescription(), minSize, maxSize));
        }
    }

    public static class InvalidContentLength extends SquadDomainException {

        public InvalidContentLength(ErrorCode errorCode, Number minSize, Number maxSize) {
            super(errorCode, String.format(errorCode.getDescription(), minSize, maxSize));
        }
    }

    public static class InvalidCapacitySize extends SquadDomainException {

        public InvalidCapacitySize(ErrorCode errorCode, Number minSize, Number maxSize) {
            super(errorCode, String.format(errorCode.getDescription(), minSize, maxSize));
        }
    }

    public static class NotEnoughLeft extends SquadDomainException {

        public NotEnoughLeft(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class UnderflowSize extends SquadDomainException {

        public UnderflowSize(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }
}
