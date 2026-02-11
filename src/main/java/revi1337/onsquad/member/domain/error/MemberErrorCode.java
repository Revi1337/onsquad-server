package revi1337.onsquad.member.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT(400, "M001", "유효하지 않은 이메일 형식입니다."),
    INVALID_NICKNAME_LENGTH(400, "M002", "닉네임은 2자 이상 8자 이하여야 합니다."),
    INVALID_PASSWORD_FORMAT(400, "M003", "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상 20자 이하여야 합니다."),
    INVALID_INTRODUCE_LENGTH(400, "M004", "자기소개는 200자 이하여야 합니다."),
    INVALID_MBTI(400, "M005", "유효하지 않은 MBTI 형식입니다."),

    DUPLICATE_NICKNAME(401, "M006", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_EMAIL(401, "M007", "이미 사용 중인 이메일입니다."),
    NON_AUTHENTICATE_EMAIL(401, "M008", "이메일 인증이 완료되지 않았습니다."),
    WRONG_PASSWORD(401, "M009", "비밀번호가 일치하지 않습니다."),

    NOT_FOUND(404, "M010", "존재하지 않는 사용자입니다.");

    private final int status;
    private final String code;
    private final String description;

}
