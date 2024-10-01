package revi1337.onsquad.crew.error.exception;

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

    public static class NotFoundById extends CrewBusinessException {

        public NotFoundById(ErrorCode errorCode, Long crewId) {
            super(errorCode, String.format(errorCode.getDescription(), crewId));
        }
    }

    public static class AlreadyExists extends CrewBusinessException {

        public AlreadyExists(ErrorCode errorCode, String crewName) {
            super(errorCode, String.format(errorCode.getDescription(), crewName));
        }
    }

    public static class AlreadyJoin extends CrewBusinessException {

        public AlreadyJoin(ErrorCode errorCode, Number crewId) {
            super(errorCode, String.format(errorCode.getDescription(), crewId));
        }
    }

    public static class InvalidPublisher extends CrewBusinessException {

        public InvalidPublisher(ErrorCode errorCode, Number crewId) {
            super(errorCode, String.format(errorCode.getDescription(), crewId));
        }
    }

    public static class OwnerCantParticipant extends CrewBusinessException {

        public OwnerCantParticipant(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
