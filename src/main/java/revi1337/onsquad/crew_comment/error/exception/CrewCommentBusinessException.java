package revi1337.onsquad.crew_comment.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CrewCommentBusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CrewCommentBusinessException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static class NotFoundById extends CrewCommentBusinessException {

        public NotFoundById(ErrorCode errorCode, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), commentId));
        }
    }

    public static class NotParent extends CrewCommentBusinessException {

        public NotParent(ErrorCode errorCode, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), commentId));
        }
    }

    public static class NotFoundCrewCrewComment extends CrewCommentBusinessException {

        public NotFoundCrewCrewComment(ErrorCode errorCode, String crewName, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), crewName, commentId));
        }
    }

}
