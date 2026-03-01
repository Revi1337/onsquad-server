package revi1337.onsquad.category.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class CategoryDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CategoryDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidCategory extends CategoryDomainException {

        public InvalidCategory(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
