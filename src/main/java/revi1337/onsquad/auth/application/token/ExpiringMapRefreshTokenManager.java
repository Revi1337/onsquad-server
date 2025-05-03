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

    private final TokenProperties tokenProperties;
    private final ExpiringMapTokenRepository tokenRepository;

    @Override
    public void saveToken(RefreshToken refreshToken, Long memberId) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        tokenRepository.save(refreshToken, memberId, expired);
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        return tokenRepository.findBy(memberId);
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        tokenRepository.deleteBy(memberId);
    }
}
