package revi1337.onsquad.auth.error.exception;

import revi1337.onsquad.auth.error.AuthException;
import revi1337.onsquad.common.error.ErrorCode;

public class InvalidTokenSignatureException extends AuthException {

    public InvalidTokenSignatureException(ErrorCode errorCode) {
        super(errorCode);
    }
}
