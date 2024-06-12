package revi1337.onsquad.auth.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USERNAME_NOT_FOUND(401, "A001", "아이디 및 비밀번호가 일치하지 않음"),
    BAD_CREDENTIAL(401, "A002", "비밀번호가 일치하지 않음"),
    INVALID_TOKEN_FORMAT(401, "A003", "토큰 포맷이 올바르지 않음"),
    INVALID_TOKEN_SIGNATURE(401, "A004", "토큰 서명이 일치하지 않음"),
    TOKEN_EXPIRED(401, "A005", "토큰 만료날짜가 지났음");

    private final int status;
    private final String code;
    private final String description;

}
