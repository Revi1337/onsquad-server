package revi1337.onsquad.squad_member.error.exception;

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

    public static class NotInSquad extends SquadMemberBusinessException {

        public NotInSquad(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class NotLeader extends SquadMemberBusinessException {

        public NotLeader(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class CannotLeaveLeader extends SquadMemberBusinessException {

        public CannotLeaveLeader(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
