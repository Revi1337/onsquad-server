package revi1337.onsquad.auth.application;

import revi1337.onsquad.auth.domain.vo.RefreshToken;

import java.util.Optional;

public interface RefreshTokenManager {

    void storeTemporaryToken(RefreshToken refreshToken, Long id);

    Optional<RefreshToken> findTemporaryToken(Long memberId);

    void updateTemporaryToken(Long memberId, RefreshToken refreshToken);

    void removeTemporaryToken(Long memberId);
    
}
