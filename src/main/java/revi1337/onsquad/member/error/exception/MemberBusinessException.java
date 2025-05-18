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

    public static class DuplicateNickname extends MemberBusinessException {

        public DuplicateNickname(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class NonAuthenticateEmail extends MemberBusinessException {

        public NonAuthenticateEmail(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class DuplicateEmail extends MemberBusinessException {

        public DuplicateEmail(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class NotFound extends MemberBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class WrongPassword extends MemberBusinessException {

        public WrongPassword(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
