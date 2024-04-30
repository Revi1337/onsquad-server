package revi1337.onsquad.auth.error;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public class AuthException extends AuthenticationException {

    private ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }
}
