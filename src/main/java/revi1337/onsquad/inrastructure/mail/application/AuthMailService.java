package revi1337.onsquad.inrastructure.mail.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepository;
import revi1337.onsquad.inrastructure.mail.support.VerificationCodeGenerator;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthMailService {

    private static final String SUBJECT = "[ONSQUAD] 회원가입 인증코드 발송 안내";
    private static final String VERIFY_CODE_SEND_LOG_FORMAT = "Starting to Send Email Verification Code - email : {} , code : {}";
    private static final String VERIFY_CODE_VERIFICATION_LOG_FORMAT = "Verifying Email Verification Code - email : {} , status : {}";
    private static final Duration VERIFICATION_CODE_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration JOINING_TIMEOUT = Duration.ofMinutes(5);

    private final EmailSender emailSender;
    private final VerificationCodeRepository repositoryChain;
    private final VerificationCodeGenerator verificationCodeGenerator;

    public void sendVerificationCode(String email) {
        String authCode = verificationCodeGenerator.generate();
        emailSender.sendEmail(SUBJECT, authCode, email);

        log.info(VERIFY_CODE_SEND_LOG_FORMAT, email, authCode);
        repositoryChain.saveVerificationCode(email, authCode, VERIFICATION_CODE_TIMEOUT);
    }

    public boolean isValidVerificationCode(String email, String authCode) {
        if (repositoryChain.isValidVerificationCode(email, authCode)) {
            log.info(VERIFY_CODE_VERIFICATION_LOG_FORMAT, email, VerificationStatus.SUCCESS.name());
            return repositoryChain.markVerificationStatus(email, VerificationStatus.SUCCESS, JOINING_TIMEOUT);
        }

        log.info(VERIFY_CODE_VERIFICATION_LOG_FORMAT, email, VerificationStatus.FAIL.name());
        return false;
    }
}
