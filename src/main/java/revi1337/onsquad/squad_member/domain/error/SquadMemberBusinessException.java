package revi1337.onsquad.squad_member.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadMemberBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadMemberBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotParticipant extends SquadMemberBusinessException {

        public NotParticipant(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class CannotLeaveLeader extends SquadMemberBusinessException {

        public CannotLeaveLeader(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class AlreadyParticipant extends SquadMemberBusinessException {

        public AlreadyParticipant(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InsufficientAuthority extends SquadMemberBusinessException {

        public InsufficientAuthority(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InvalidRequest extends SquadMemberBusinessException {

        public InvalidRequest(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
