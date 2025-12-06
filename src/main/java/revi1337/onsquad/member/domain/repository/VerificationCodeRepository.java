package revi1337.onsquad.member.domain.repository;

import java.time.Duration;
import revi1337.onsquad.member.domain.model.VerificationStatus;

public interface VerificationCodeRepository {

    long saveVerificationCode(String email, String verificationCode, Duration minutes);

    boolean isValidVerificationCode(String email, String verificationCode);

    boolean markVerificationStatus(String email, VerificationStatus verificationStatus, Duration minutes);

    boolean isMarkedVerificationStatusWith(String email, VerificationStatus verificationStatus);

}
