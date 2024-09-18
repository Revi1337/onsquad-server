package revi1337.onsquad.crew_comment.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CrewCommentDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CrewCommentDomainException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static class InvalidLength extends CrewCommentDomainException {

        public InvalidLength(ErrorCode errorCode, int maxLength) {
            super(errorCode, String.format(errorCode.getDescription(), maxLength));
        }
    }

}
