package revi1337.onsquad.auth.application.token;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.auth.repository.token.ExpiringMapTokenRepository;

@RequiredArgsConstructor
@Service
public class ExpiringMapRefreshTokenManager implements RefreshTokenManager {

    private final ExpiringMapTokenRepository refreshTokenOperation;
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
