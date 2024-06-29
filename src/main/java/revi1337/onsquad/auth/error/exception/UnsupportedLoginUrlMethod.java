package revi1337.onsquad.auth.error.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public class UnsupportedLoginUrlMethod extends AuthenticationException {

    private ErrorCode errorCode;

    public UnsupportedLoginUrlMethod(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public UnsupportedLoginUrlMethod(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }
}
