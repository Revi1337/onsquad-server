package revi1337.onsquad.inrastructure.s3.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class S3BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public S3BusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class UploadFail extends S3BusinessException {

        public UploadFail(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

}
