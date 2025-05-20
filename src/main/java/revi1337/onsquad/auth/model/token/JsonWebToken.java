package revi1337.onsquad.auth.model.token;

public record JsonWebToken(
        String accessToken,
        String refreshToken
) {
    public static JsonWebToken create(AccessToken accessToken, RefreshToken refreshToken) {
        return new JsonWebToken(accessToken.value(), refreshToken.value());
    }
}
