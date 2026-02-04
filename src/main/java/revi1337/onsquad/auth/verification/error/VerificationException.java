package revi1337.onsquad.auth.verification.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class VerificationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public VerificationException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class UnAuthenticateVerificationCode extends VerificationException {

        public UnAuthenticateVerificationCode(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }
}
