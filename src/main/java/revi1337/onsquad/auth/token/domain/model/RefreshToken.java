package revi1337.onsquad.auth.token.domain.model;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public record RefreshToken(Long identifier, String value, Date expiredAt) {

    public RefreshToken(String value) {
        this(null, value, null);
    }

    public boolean isAvailableAt(Instant now) {
        if (expiredAt == null) {
            return false;
        }
        return expiredAt.getTime() > now.toEpochMilli();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RefreshToken that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
