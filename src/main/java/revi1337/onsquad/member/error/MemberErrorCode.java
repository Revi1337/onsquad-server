package revi1337.onsquad.member.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    NON_AUTHENTICATE_EMAIL(401, "M001", "메일 인증이 되어있지 않은 상태"),
    DUPLICATE_NICKNAME(400, "M002", "닉네임이 중복된 상태"),
    INVALID_EMAIL_FORMAT(400, "M003", "이메일 형식이 올바르지 않은 상태"),
    INVALID_NICKNAME_LENGTH(400, "M004", "닉네임 길이가 올바르지 않은 상태");

    private final int status;
    private final String code;
    private final String description;

}
