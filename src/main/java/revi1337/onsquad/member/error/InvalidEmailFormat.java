package revi1337.onsquad.member.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public class InvalidEmailFormat extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidEmailFormat(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
