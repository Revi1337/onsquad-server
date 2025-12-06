package revi1337.onsquad.member.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import revi1337.onsquad.common.application.mail.EmailContent;

public class VerificationCode implements EmailContent {

    private final String code;
    private final LocalDateTime expiredAt;

    public VerificationCode(String code, long expireMilli) {
        this.code = code;
        this.expiredAt = Instant.ofEpochMilli(expireMilli)
                .atZone(TimeZone.getDefault().toZoneId())
                .toLocalDateTime();
    }

    @Override
    public String getContent() {
        return code;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }
}
