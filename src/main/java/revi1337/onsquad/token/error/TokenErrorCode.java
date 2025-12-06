package revi1337.onsquad.token.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {

    INVALID_TOKEN_FORMAT(401, "T001", "토큰 포맷이 올바르지 않습니다."),
    INVALID_TOKEN_SIGNATURE(401, "T002", "토큰 서명이 일치하지 않습니다."),
    TOKEN_EXPIRED(401, "T003", "토큰이 만료되었습니다."),
    EMPTY_TOKEN(401, "T004", "토큰이 필요한 API 입니다."),
    NOT_FOUND_REFRESH(401, "T005", "리프레시 토큰을 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_MEMBER(401, "T006", "리프레시 토큰에 명시된 사용자를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
