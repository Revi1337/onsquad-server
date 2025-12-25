package revi1337.onsquad.squad_comment.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadCommentDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadCommentDomainException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static class InvalidLength extends SquadCommentDomainException {

        public InvalidLength(ErrorCode errorCode, int maxLength) {
            super(errorCode, String.format(errorCode.getDescription(), maxLength));
        }
    }

}
