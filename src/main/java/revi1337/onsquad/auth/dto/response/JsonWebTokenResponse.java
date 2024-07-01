package revi1337.onsquad.auth.dto.response;

import revi1337.onsquad.auth.domain.vo.AccessToken;
import revi1337.onsquad.auth.domain.vo.RefreshToken;

public record JsonWebTokenResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) {
    public static JsonWebTokenResponse of(AccessToken accessToken, RefreshToken refreshToken) {
        return new JsonWebTokenResponse(accessToken, refreshToken);
    }
}
