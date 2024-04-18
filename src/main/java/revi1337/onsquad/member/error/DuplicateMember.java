package revi1337.onsquad.member.error;

import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.member.error.exception.MemberException;

public class DuplicateMember extends MemberException {

    public DuplicateMember(ErrorCode errorCode) {
        super(errorCode);
    }
}
