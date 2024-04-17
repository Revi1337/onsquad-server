package revi1337.onsquad.member.error;

import revi1337.onsquad.common.error.ErrorCode;

public class DuplicateNickname extends MemberException {

    public DuplicateNickname(ErrorCode errorCode) {
        super(errorCode);
    }
}
