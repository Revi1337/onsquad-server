package revi1337.onsquad.auth.verification.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum VerificationErrorCode implements ErrorCode {

    EMAIL_UNAUTHENTICATE(401, "V001", "이메일 인증이 되어있지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
