package revi1337.onsquad.token.domain.model;

import java.util.Date;
import java.util.Objects;

public record AccessToken(String value, Date expiredAt) {

    public AccessToken(String value) {
        this(value, null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccessToken that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
