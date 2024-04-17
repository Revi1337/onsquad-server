package revi1337.onsquad.member.error.exception;

import revi1337.onsquad.common.error.ErrorCode;

public class UnsatisfiedEmailAuthentication extends MemberException {

    public UnsatisfiedEmailAuthentication(ErrorCode errorCode) {
        super(errorCode);
    }
}
