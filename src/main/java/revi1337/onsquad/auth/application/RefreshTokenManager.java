package revi1337.onsquad.auth.application;

import revi1337.onsquad.auth.domain.vo.RefreshToken;

import java.util.Optional;

public interface RefreshTokenManager {

    void storeTemporaryToken(RefreshToken refreshToken, Long id);

    Optional<Long> findTemporaryToken(RefreshToken refreshToken);

    void removeTemporaryToken(RefreshToken refreshToken);
    
}
