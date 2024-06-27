package revi1337.onsquad.comment.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CommentBusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CommentBusinessException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static class NotFoundById extends CommentBusinessException {

        public NotFoundById(ErrorCode errorCode, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), commentId));
        }
    }

    public static class NotParent extends CommentBusinessException {

        public NotParent(ErrorCode errorCode, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), commentId));
        }
    }

    public static class NotFoundCrewComment extends CommentBusinessException {

        public NotFoundCrewComment(ErrorCode errorCode, String crewName, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), crewName, commentId));
        }
    }

}
