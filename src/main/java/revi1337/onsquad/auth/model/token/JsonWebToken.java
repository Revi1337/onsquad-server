package revi1337.onsquad.auth.model.token;

public record JsonWebToken(
        AccessToken accessToken,
        RefreshToken refreshToken
) {
    public static JsonWebToken of(AccessToken accessToken, RefreshToken refreshToken) {
        return new JsonWebToken(accessToken, refreshToken);
    }
}
