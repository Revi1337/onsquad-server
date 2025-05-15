package revi1337.onsquad.crew_member.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CrewMemberBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CrewMemberBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotParticipant extends CrewMemberBusinessException {

        public NotParticipant(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class NotOwner extends CrewMemberBusinessException {

        public NotOwner(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class LessThenManager extends CrewMemberBusinessException {

        public LessThenManager(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
