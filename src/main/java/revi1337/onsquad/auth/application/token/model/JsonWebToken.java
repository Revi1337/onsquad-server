package revi1337.onsquad.auth.application.token.model;

public record JsonWebToken(
        String accessToken,
        String refreshToken
) {
    public static JsonWebToken create(AccessToken accessToken, RefreshToken refreshToken) {
        return new JsonWebToken(accessToken.value(), refreshToken.value());
    }
}
