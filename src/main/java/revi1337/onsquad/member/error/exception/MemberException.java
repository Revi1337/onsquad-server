package revi1337.onsquad.member.error.exception;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class MemberException extends RuntimeException {

    private final ErrorCode errorCode;

    public MemberException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
