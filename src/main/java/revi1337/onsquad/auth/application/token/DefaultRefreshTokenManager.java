package revi1337.onsquad.auth.application.token;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.repository.token.TokenRepositoryComposite;

@RequiredArgsConstructor
@Component
public class DefaultRefreshTokenManager implements RefreshTokenManager {

    private final TokenProperties tokenProperties;
    private final TokenRepositoryComposite tokenRepositoryComposite;

    @Override
    public void saveTokenFor(Long memberId, RefreshToken refreshToken) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        tokenRepositoryComposite.save(refreshToken, memberId, expired);
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        return tokenRepositoryComposite.findBy(memberId);
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        tokenRepositoryComposite.deleteBy(memberId);
    }
}
