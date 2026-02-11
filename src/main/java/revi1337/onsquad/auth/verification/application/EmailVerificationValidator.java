package revi1337.onsquad.auth.verification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.auth.verification.domain.error.VerificationErrorCode;
import revi1337.onsquad.auth.verification.domain.error.VerificationException;

@Component
@RequiredArgsConstructor
public class EmailVerificationValidator {

    private final VerificationCodeStorage redisVerificationCodeStorage;

    public void ensureEmailVerified(String email) {
        if (!redisVerificationCodeStorage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)) {
            throw new VerificationException.UnAuthenticateVerificationCode(VerificationErrorCode.EMAIL_UNAUTHENTICATE);
        }
    }
}
