package revi1337.onsquad.auth.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class AuthJoinException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public AuthJoinException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class DuplicateNickname extends AuthJoinException {

        public DuplicateNickname(ErrorCode errorCode, String nickname) {
            super(errorCode, String.format(errorCode.getDescription(), nickname));
        }
    }

    public static class NonAuthenticateEmail extends AuthJoinException {

        public NonAuthenticateEmail(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class DuplicateMember extends AuthJoinException {

        public DuplicateMember(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
