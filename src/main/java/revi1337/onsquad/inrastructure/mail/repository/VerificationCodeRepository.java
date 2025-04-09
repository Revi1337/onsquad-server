package revi1337.onsquad.inrastructure.mail.repository;

import java.time.Duration;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;

public interface VerificationCodeRepository {

    void saveVerificationCode(String email, String verificationCode, Duration minutes);

    boolean isValidVerificationCode(String email, String verificationCode);

    boolean markVerificationStatus(String email, VerificationStatus verificationStatus, Duration minutes);

    boolean isMarkedVerificationStatusWith(String email, VerificationStatus verificationStatus);

}