package revi1337.onsquad.auth.dto.response;

public record JsonWebTokenResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) {
    public static JsonWebTokenResponse of(AccessToken accessToken, RefreshToken refreshToken) {
        return new JsonWebTokenResponse(accessToken, refreshToken);
    }
}
