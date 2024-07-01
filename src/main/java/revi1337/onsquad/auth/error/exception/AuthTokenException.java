package revi1337.onsquad.auth.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class AuthTokenException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public static class InvalidTokenFormat extends AuthTokenException {

        public InvalidTokenFormat(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class InvalidTokenSignature extends AuthTokenException {

        public InvalidTokenSignature(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class TokenExpired extends AuthTokenException {

        public TokenExpired(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class NeedToken extends AuthTokenException {

        public NeedToken(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class InvalidRefreshOwner extends AuthTokenException {

        public InvalidRefreshOwner(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class NotFoundRefreshOwner extends AuthTokenException {

        public NotFoundRefreshOwner(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
