package revi1337.onsquad.comment.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CommentDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CommentDomainException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static class InvalidLength extends CommentDomainException {

        public InvalidLength(ErrorCode errorCode, int maxLength) {
            super(errorCode, String.format(errorCode.getDescription(), maxLength));
        }
    }

}
