package revi1337.onsquad.inrastructure.mail.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.inrastructure.mail.domain.AuthMailRedisRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthMailManager {

    private static final String SUBJECT = "[ONSQUAD] 회원가입 인증코드 발송 안내";
    private final EmailSender emailSender;
    private final AuthMailRedisRepository authMailRedisRepository;

    public void sendAuthCodeToEmail(String email, String authCode, Duration expired) {
        emailSender.sendEmail(SUBJECT, authCode, email);
        log.info("[MAIL CODE] EMAIL : {}, PUBLISH : {}", email, authCode);
        authMailRedisRepository.saveAuthCode(email, authCode, expired);
    }

    public boolean verifyAuthCode(String email, String confirm, Duration minutes) {
        if (!authMailRedisRepository.isValidMailAuthCode(email, confirm)) {
            log.info("[MAIL CODE] EMAIL : {}, VALID : FAIL", email);
            return false;
        }

        log.info("[MAIL CODE] EMAIL : {}, VALID : SUCCESS", email);
        return authMailRedisRepository.overwriteAuthCodeToStatus(email, MailStatus.SUCCESS, minutes);
    }

    public boolean isValidMailStatus(String email) {
        return authMailRedisRepository.isValidMailStatus(email, MailStatus.SUCCESS);
    }
}
