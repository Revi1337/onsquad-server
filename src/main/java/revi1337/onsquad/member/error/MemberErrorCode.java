package revi1337.onsquad.member.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT(400, "M001", "이메일 형식이 올바르지 않습니다."),
    INVALID_NICKNAME_LENGTH(400, "M002", "닉네임은 2 ~ 8 길이여야 합니다."),
    INVALID_PASSWORD_FORMAT(400, "M003", "비밀번호는 영문,숫자,특수문자 조합 8 ~ 20 길이여야 합니다."),
    INVALID_INTRODUCE_LENGTH(400, "M004", "한줄 소개는 1 ~ 200 자 이하여야 합니다."),
    INVALID_MBTI(400, "M005", "존재하지 않는 MBTI 입니다."),

    DUPLICATE_NICKNAME(401, "M006", "이미 사용중인 닉네임입니다."),
    DUPLICATE_EMAIL(401, "M007", "이미 사용중인 이메일입니다."),
    NON_AUTHENTICATE_EMAIL(401, "M008", "이메일 인증이 되어있지 않습니다."),

    WRONG_PASSWORD(401, "M009", "비밀번호가 일치하지 않습니다."),
    NOTFOUND(404, "M010", "사용자를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
