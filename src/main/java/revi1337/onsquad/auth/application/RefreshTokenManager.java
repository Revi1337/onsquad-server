package revi1337.onsquad.auth.application;

import java.util.Optional;
import revi1337.onsquad.auth.application.token.RefreshToken;

public interface RefreshTokenManager {

    void storeTemporaryToken(RefreshToken refreshToken, Long memberId);

    Optional<RefreshToken> findTemporaryToken(Long memberId);

    void removeTemporaryToken(Long memberId);

}
