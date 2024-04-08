package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import revi1337.onsquad.common.mail.EmailSender;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class JoinMailService {

    private final EmailSender emailSender;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY_FORMAT = "onsquad:auth:mail:%s";
    private static final String SUBJECT = "[ONSQUAD] 회원가입 인증코드 발송 안내";

    public void sendAuthCodeToEmail(String email, String authCode) {
        emailSender.sendEmail(SUBJECT, authCode, email);
        log.info("[MAIL CODE] USER : {} --> PUBLISH : {}", email, authCode);
        stringRedisTemplate
                .opsForValue()
                .set(String.format(KEY_FORMAT, email), authCode, Duration.ofMinutes(3));
    }
}
