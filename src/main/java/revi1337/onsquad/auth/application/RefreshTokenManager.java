package revi1337.onsquad.auth.application;

import java.util.Optional;
import revi1337.onsquad.auth.application.token.RefreshToken;

public interface RefreshTokenManager {

    void storeTemporaryToken(RefreshToken refreshToken, Long id);

    Optional<RefreshToken> findTemporaryToken(Long memberId);

    void updateTemporaryToken(Long memberId, RefreshToken refreshToken);

    void removeTemporaryToken(Long memberId);

}
