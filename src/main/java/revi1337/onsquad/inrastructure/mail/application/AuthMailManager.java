package revi1337.onsquad.inrastructure.mail.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.inrastructure.mail.domain.RedisMailRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class JoinMailService {

    private static final String SUBJECT = "[ONSQUAD] 회원가입 인증코드 발송 안내";
    private final EmailSender emailSender;
    private final RedisMailRepository redisMailRepository;

    // TODO Duration 을 외부에서 주입받도록 변경 필요.
    public void sendAuthCodeToEmail(String email, String authCode) {
        emailSender.sendEmail(SUBJECT, authCode, email);
        log.info("[MAIL CODE] EMAIL : {}, PUBLISH : {}", email, authCode);
        redisMailRepository.saveAuthCode(email, authCode, Duration.ofMinutes(3));
    }

    public boolean verifyAuthCode(String email, String confirm, Duration minutes) {
        if (!redisMailRepository.isValidMailAuthCode(email, confirm)) {
            log.info("[MAIL CODE] EMAIL : {}, VALID : FAIL", email);
            return false;
        }

        log.info("[MAIL CODE] EMAIL : {}, VALID : SUCCESS", email);
        return redisMailRepository.overwriteAuthCodeToStatus(email, MailStatus.SUCCESS, minutes);
    }

    public boolean isValidMailStatus(String email) {
        return redisMailRepository.isValidMailStatus(email, MailStatus.SUCCESS);
    }
}
