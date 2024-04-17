package revi1337.onsquad.member.error.exception;

import revi1337.onsquad.common.error.ErrorCode;

public class UnsatisfiedNicknameLength extends MemberException {

    public UnsatisfiedNicknameLength(ErrorCode errorCode) {
        super(errorCode);
    }
}
