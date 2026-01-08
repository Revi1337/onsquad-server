package revi1337.onsquad.auth.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.verification.domain.VerificationCodeRepository;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.member.error.MemberBusinessException;
import revi1337.onsquad.member.error.MemberErrorCode;

@RequiredArgsConstructor
@Service
public class EmailVerificationValidator {

    private final VerificationCodeRepository verificationCodeRedisRepository;

    public void ensureEmailVerified(String email) {
        if (!verificationCodeRedisRepository.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)) {
            throw new MemberBusinessException.NonAuthenticateEmail(MemberErrorCode.NON_AUTHENTICATE_EMAIL);
        }
    }
}
