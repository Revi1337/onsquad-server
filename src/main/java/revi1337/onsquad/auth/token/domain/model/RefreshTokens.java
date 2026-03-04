package revi1337.onsquad.auth.token.domain.model;

import java.time.Instant;
import java.util.List;

public record RefreshTokens(List<RefreshToken> refreshTokens) {

    public boolean isEmpty() {
        return refreshTokens.isEmpty();
    }

    public List<RefreshToken> extractAvailableBefore(Instant now) {
        return refreshTokens.stream()
                .filter(refreshToken -> refreshToken.isAvailableAt(now))
                .toList();
    }
}
