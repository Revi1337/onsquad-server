package revi1337.onsquad.inrastructure.mail.application;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

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
