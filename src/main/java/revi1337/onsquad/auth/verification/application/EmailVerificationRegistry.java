package revi1337.onsquad.auth.verification.application;

import java.time.Duration;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

public interface EmailVerificationRegistry {

    long register(String email, String code, VerificationStatus status, Duration expireDuration);

    boolean isCodeMatched(String email, String code);

    boolean changeStatus(String email, VerificationStatus status, Duration expireDuration);

    boolean confirm(String email, String authCode, Duration expireDuration);

    boolean hasStatus(String email, VerificationStatus status);

}
