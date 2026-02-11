package revi1337.onsquad.token.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class TokenException extends RuntimeException {

    private final ErrorCode errorCode;

    public TokenException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public static class InvalidTokenFormat extends TokenException {

        public InvalidTokenFormat(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class InvalidTokenSignature extends TokenException {

        public InvalidTokenSignature(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class TokenExpired extends TokenException {

        public TokenExpired(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class NeedToken extends TokenException {

        public NeedToken(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class NotFoundRefresh extends TokenException {

        public NotFoundRefresh(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class NotFoundRefreshOwner extends TokenException {

        public NotFoundRefreshOwner(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
