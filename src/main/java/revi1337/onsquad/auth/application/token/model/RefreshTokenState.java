package revi1337.onsquad.auth.application.token.model;

public record RefreshTokenState(
        RefreshToken value,
        String memberId,
        long expireTime
) {
}
