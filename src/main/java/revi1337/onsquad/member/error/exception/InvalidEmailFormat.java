package revi1337.onsquad.member.error.exception;

import revi1337.onsquad.common.error.ErrorCode;

public class InvalidEmailFormat extends MemberException {

    public InvalidEmailFormat(ErrorCode errorCode) {
        super(errorCode);
    }
}
