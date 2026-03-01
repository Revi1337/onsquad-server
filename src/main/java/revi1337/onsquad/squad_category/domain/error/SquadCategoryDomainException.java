package revi1337.onsquad.squad_category.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class SquadCategoryDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public SquadCategoryDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidCategorySize extends SquadCategoryDomainException {

        public InvalidCategorySize(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
