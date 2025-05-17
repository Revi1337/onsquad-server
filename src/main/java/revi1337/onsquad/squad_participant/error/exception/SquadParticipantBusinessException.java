package revi1337.onsquad.squad_participant.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadParticipantBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadParticipantBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NeverRequested extends SquadParticipantBusinessException {

        public NeverRequested(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class MismatchReference extends SquadParticipantBusinessException {
        public MismatchReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
