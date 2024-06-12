package revi1337.onsquad.auth.error.exception;

import revi1337.onsquad.auth.error.AuthException;
import revi1337.onsquad.common.error.ErrorCode;

public class TokenExpiredException extends AuthException {

    public TokenExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
