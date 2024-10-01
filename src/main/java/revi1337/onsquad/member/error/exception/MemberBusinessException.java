package revi1337.onsquad.member.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class MemberBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public MemberBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotFound extends MemberBusinessException {

        public NotFound(ErrorCode errorCode, Number memberId) {
            super(errorCode, String.format(errorCode.getDescription(), memberId));
        }
    }

    public static class WrongPassword extends MemberBusinessException {

        public WrongPassword(ErrorCode errorCode, Number memberId) {
            super(errorCode, String.format(errorCode.getDescription(), memberId));
        }
    }
}
