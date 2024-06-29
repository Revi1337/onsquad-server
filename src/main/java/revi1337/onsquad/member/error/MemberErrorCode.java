package revi1337.onsquad.member.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT(400, "M001", "이메일 형식이 올바르지 않은 상태"),
    INVALID_NICKNAME_LENGTH(400, "M002", "닉네임 길이가 올바르지 않은 상태"),
    DUPLICATE_NICKNAME(401, "M003", "닉네임이 중복된 상태"),
    NON_AUTHENTICATE_EMAIL(401, "M004", "메일 인증이 되어있지 않은 상태"),
    DUPLICATE_MEMBER(401, "M005", "이미 회원가입이 되어있는 사용자"),
    INVALID_PASSWORD_FORMAT(400, "M006", "비밀번호는 영문,숫자,특수문자 조합 8 ~ 20 길이어야 한다."),

    NOTFOUND(404, "M007", "id 가 %d 인 사용자를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
