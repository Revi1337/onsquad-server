package revi1337.onsquad.auth.error.exception;

import revi1337.onsquad.auth.error.AuthException;
import revi1337.onsquad.common.error.ErrorCode;

public class UnsupportedLoginUrlMethod extends AuthException {

    public UnsupportedLoginUrlMethod(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
