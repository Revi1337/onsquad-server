package revi1337.onsquad.auth.error.exception;

import revi1337.onsquad.auth.error.AuthException;
import revi1337.onsquad.common.error.ErrorCode;

public class InvalidTokenFormatException extends AuthException {

    public InvalidTokenFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
