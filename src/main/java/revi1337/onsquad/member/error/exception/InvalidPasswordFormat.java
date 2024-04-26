package revi1337.onsquad.member.error.exception;

import revi1337.onsquad.common.error.ErrorCode;

public class InvalidPasswordFormat extends MemberException {

    public InvalidPasswordFormat(ErrorCode errorCode) {
        super(errorCode);
    }
}
