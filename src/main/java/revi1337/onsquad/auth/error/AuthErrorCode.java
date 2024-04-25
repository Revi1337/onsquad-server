package revi1337.onsquad.auth.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USERNAME_NOT_FOUND(401, "A001", "존재하지 않는 사용자."),
    BAD_CREDENTIAL(401, "A002", "비밀번호가 일치하지 않음."),
    UNSUPPORTED_LOGIN_METHOD(405, "A003", "지원하지 않는 HTTP Method.");

    private final int status;
    private final String code;
    private final String description;

}
