package revi1337.onsquad.auth.verification.application;

import java.time.Duration;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

public interface VerificationCodeStorage {

    long saveVerificationCode(String email, String code, VerificationStatus status, Duration minutes);

    boolean isValidVerificationCode(String email, String code);

    boolean markVerificationStatus(String email, VerificationStatus status, Duration minutes);

    boolean isMarkedVerificationStatusWith(String email, VerificationStatus status);

}
