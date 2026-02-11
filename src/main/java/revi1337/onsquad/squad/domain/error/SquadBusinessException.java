package revi1337.onsquad.squad.domain.error;

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

    public static class AlreadyParticipant extends SquadBusinessException {

        public AlreadyParticipant(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class MismatchReference extends SquadBusinessException {

        public MismatchReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InsufficientAuthority extends SquadBusinessException {

        public InsufficientAuthority(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
