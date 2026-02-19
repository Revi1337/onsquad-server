package revi1337.onsquad.auth.verification.application;

import java.time.Duration;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

public interface VerificationCodeStorage {

    long saveVerificationCode(String email, String code, VerificationStatus status, Duration expireDuration);

    boolean isValidVerificationCode(String email, String code);

    boolean markVerificationStatus(String email, VerificationStatus status, Duration expireDuration);

    boolean markVerificationStatusAsSuccess(String email, String authCode, Duration expireDuration);

    boolean isMarkedVerificationStatusWith(String email, VerificationStatus status);

}
