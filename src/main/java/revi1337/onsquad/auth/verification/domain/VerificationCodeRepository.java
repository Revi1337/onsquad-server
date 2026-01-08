package revi1337.onsquad.auth.verification.domain;

import java.time.Duration;

public interface VerificationCodeRepository {

    long saveVerificationCode(String email, String verificationCode, Duration minutes);

    boolean isValidVerificationCode(String email, String verificationCode);

    boolean markVerificationStatus(String email, VerificationStatus verificationStatus, Duration minutes);

    boolean isMarkedVerificationStatusWith(String email, VerificationStatus verificationStatus);

}
