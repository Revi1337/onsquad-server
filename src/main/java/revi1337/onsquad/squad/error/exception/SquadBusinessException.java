package revi1337.onsquad.squad.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotFound extends SquadBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class AlreadyRequest extends SquadBusinessException {

        public AlreadyRequest(ErrorCode errorCode, String squadName) {
            super(errorCode, String.format(errorCode.getDescription(), squadName));
        }
    }

    public static class AlreadyParticipant extends SquadBusinessException {

        public AlreadyParticipant(ErrorCode errorCode, String squadName) {
            super(errorCode, String.format(errorCode.getDescription(), squadName));
        }
    }

    public static class NotInSquad extends SquadBusinessException {

        public NotInSquad(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class OwnerCantParticipant extends SquadBusinessException {

        public OwnerCantParticipant(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class MismatchReference extends SquadBusinessException {

        public MismatchReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
