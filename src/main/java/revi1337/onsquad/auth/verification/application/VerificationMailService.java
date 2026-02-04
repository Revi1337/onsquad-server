package revi1337.onsquad.auth.verification.application;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.application.mail.EmailSender;
import revi1337.onsquad.infrastructure.network.mail.EmailException;

@Slf4j
@Service
public class VerificationMailService {

    private static final String MAIL_SUBJECT = "[ONSQUAD] 회원가입 인증코드 발송 안내";
    private static final String SEND_VERIFICATION_CODE_SUCCESS_LOG_FORMAT = "[이메일 인증코드 발송 성공] - email : {}, code : {}";
    private static final String SEND_VERIFICATION_CODE_FAIL_LOG_FORMAT = "[이메일 인증코드 발송 실패] - email : {}, cause : {}";
    private static final String VERIFY_VERIFICATION_CODE_LOG_FORMAT = "[이메일 인증코드 검증] - email : {}, status : {}";
    private static final Duration VERIFICATION_CODE_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration JOINING_TIMEOUT = Duration.ofMinutes(5);

    public VerificationMailService(
            @Qualifier("verificationCodeEmailSender") EmailSender emailSender,
            VerificationCodeStorage redisVerificationCodeStorage,
            VerificationCodeGenerator verificationCodeGenerator
    ) {
        this.emailSender = emailSender;
        this.redisVerificationCodeStorage = redisVerificationCodeStorage;
        this.verificationCodeGenerator = verificationCodeGenerator;
    }

    private final EmailSender emailSender;
    private final VerificationCodeStorage redisVerificationCodeStorage;
    private final VerificationCodeGenerator verificationCodeGenerator;

    @Async("sending-verification-code-executor")
    public void sendVerificationCode(String email) {
        try {
            String authCode = verificationCodeGenerator.generate();
            long expireMilli = redisVerificationCodeStorage.saveVerificationCode(email, authCode, VerificationStatus.PENDING, VERIFICATION_CODE_TIMEOUT);

            emailSender.sendEmail(MAIL_SUBJECT, new VerificationCode(email, authCode, VerificationStatus.PENDING, expireMilli), email);
            log.info(SEND_VERIFICATION_CODE_SUCCESS_LOG_FORMAT, email, authCode);
        } catch (EmailException e) {
            log.error(SEND_VERIFICATION_CODE_FAIL_LOG_FORMAT, email, e.getMessage());
        }
    }

    public boolean validateVerificationCode(String email, String authCode) {
        if (redisVerificationCodeStorage.isValidVerificationCode(email, authCode)) {
            boolean mark = redisVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, JOINING_TIMEOUT);
            log.info(VERIFY_VERIFICATION_CODE_LOG_FORMAT, email, VerificationStatus.SUCCESS.name());
            return mark;
        }

        log.info(VERIFY_VERIFICATION_CODE_LOG_FORMAT, email, VerificationStatus.FAIL.name());
        return false;
    }
}
