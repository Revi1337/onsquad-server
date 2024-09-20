package revi1337.onsquad.squad_comment.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadCommentBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadCommentBusinessException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static class NotFoundById extends SquadCommentBusinessException {

        public NotFoundById(ErrorCode errorCode, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), commentId));
        }
    }

    public static class NotParent extends SquadCommentBusinessException {

        public NotParent(ErrorCode errorCode, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), commentId));
        }
    }

    public static class NotFoundSquadComment extends SquadCommentBusinessException {

        public NotFoundSquadComment(ErrorCode errorCode, Number crewId, Number commentId) {
            super(errorCode, String.format(errorCode.getDescription(), crewId, commentId));
        }
    }

}
