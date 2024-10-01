package revi1337.onsquad.auth.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USERNAME_NOT_FOUND(401, "A001", "존재하지 않는 이메일입니다."),
    BAD_CREDENTIAL(401, "A002", "비밀번호가 일치하지 않습니다."),

    DUPLICATE_NICKNAME(401, "A003", "%s 닉네임은 이미 사용중입니다."),
    NON_AUTHENTICATE_EMAIL(401, "A004", "메일 인증이 되어있지 않습니다."),
    DUPLICATE_MEMBER(401, "A005", "이미 회원가입이 되어있는 사용자입니다.");

    private final int status;
    private final String code;
    private final String description;

}
