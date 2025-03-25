package revi1337.onsquad.auth.application;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.application.token.RefreshToken;
import revi1337.onsquad.auth.config.properties.TokenProperties;

@RequiredArgsConstructor
@Service
public class ExpiringMapRefreshTokenManager implements RefreshTokenManager {

    private final ExpiringMapTokenOperation refreshTokenOperation;
    private final TokenProperties tokenProperties;

    @Override
    public void storeTemporaryToken(RefreshToken refreshToken, Long memberId) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        refreshTokenOperation.storeTemporaryRefreshToken(refreshToken, memberId, expired);
    }

    @Override
    public Optional<RefreshToken> findTemporaryToken(Long memberId) {
        return refreshTokenOperation.retrieveTemporaryRefreshToken(memberId);
    }

    @Override
    public void removeTemporaryToken(Long memberId) {
        refreshTokenOperation.deleteTemporaryRefreshToken(memberId);
    }
}
