package revi1337.onsquad.squad_request.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadRequestBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadRequestBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class NotFound extends SquadRequestBusinessException {

        public NotFound(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class MismatchReference extends SquadRequestBusinessException {

        public MismatchReference(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InsufficientAuthority extends SquadRequestBusinessException {

        public InsufficientAuthority(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
