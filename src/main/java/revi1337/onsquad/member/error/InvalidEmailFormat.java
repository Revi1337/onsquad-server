package revi1337.onsquad.member.error;

import revi1337.onsquad.common.error.ErrorCode;

public class InvalidEmailFormat extends MemberException {

    public InvalidEmailFormat(ErrorCode errorCode) {
        super(errorCode);
    }
}
