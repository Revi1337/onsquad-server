package revi1337.onsquad.auth.application.dto;

import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.application.token.RefreshToken;

public record JsonWebToken(
        AccessToken accessToken,
        RefreshToken refreshToken
) {
    public static JsonWebToken of(AccessToken accessToken, RefreshToken refreshToken) {
        return new JsonWebToken(accessToken, refreshToken);
    }
}
