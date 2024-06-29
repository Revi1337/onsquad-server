package revi1337.onsquad.member.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT(400, "M001", "이메일 형식이 올바르지 않습니다."),
    INVALID_NICKNAME_LENGTH(400, "M002", "닉네임은 %d 자 이상 %d 자 이하여야합니다."),
    DUPLICATE_NICKNAME(401, "M003", "%s 닉네임은 이미 사용중입니다."),
    INVALID_PASSWORD_FORMAT(400, "M004", "비밀번호는 영문,숫자,특수문자 조합 8 ~ 20 길이어야 합니다."),

    NOTFOUND(404, "M005", "id 가 %d 인 사용자를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
