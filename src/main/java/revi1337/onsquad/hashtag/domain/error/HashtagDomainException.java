package revi1337.onsquad.hashtag.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class HashtagDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public HashtagDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidHashtag extends HashtagDomainException {

        public InvalidHashtag(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
