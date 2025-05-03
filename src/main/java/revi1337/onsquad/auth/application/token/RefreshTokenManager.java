package revi1337.onsquad.auth.application.token;

import java.util.Optional;
import revi1337.onsquad.auth.model.token.RefreshToken;

public interface RefreshTokenManager {

    void saveToken(RefreshToken refreshToken, Long memberId);

    Optional<RefreshToken> findTokenBy(Long memberId);

    void deleteTokenBy(Long memberId);

}
