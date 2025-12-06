package revi1337.onsquad.auth.security.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(401, "A001", "이메일 또는 비밀번호가 일치하지 않습니다."),
    USERNAME_NOT_FOUND(401, "A002", "존재하지 않는 이메일입니다."),
    BAD_CREDENTIAL(401, "A003", "비밀번호가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
