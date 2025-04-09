package revi1337.onsquad.inrastructure.file.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class FileControlException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public FileControlException(ErrorCode errorCode, String finalErrorMessage, Throwable cause) {
        super(finalErrorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class FailProcess extends FileControlException {

        public FailProcess(ErrorCode errorCode, Throwable cause) {
            super(errorCode, String.format(errorCode.getDescription()), cause);
        }
    }
}
