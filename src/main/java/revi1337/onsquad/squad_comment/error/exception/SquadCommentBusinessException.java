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

    public static class NotFound extends SquadCommentBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class NotParent extends SquadCommentBusinessException {

        public NotParent(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class NonMatchWriterId extends SquadCommentBusinessException {

        public NonMatchWriterId(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class Deleted extends SquadCommentBusinessException {

        public Deleted(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
