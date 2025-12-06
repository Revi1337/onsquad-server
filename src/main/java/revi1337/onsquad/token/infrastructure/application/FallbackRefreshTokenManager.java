package revi1337.onsquad.token.infrastructure.application;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.token.application.RefreshTokenManager;
import revi1337.onsquad.token.config.TokenProperties;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.infrastructure.repository.TokenRepositoryComposite;

/**
 * @see RedisRefreshTokenManager
 * @deprecated <p>
 * Issues with the composite fallback approach:
 * </p>
 *
 * <ul>
 *     <li>It is unclear how to reliably detect when a repository has actually failed.</li>
 *     <li>Handling idempotency across multiple fallback repositories is difficult.</li>
 *     <li>Automatically falling back to ExpiringMap during a Redis outage can lead to an
 *         inconsistent and partially distributed system.</li>
 *     <li>The behavior implicitly depends on the ordering of the repositories, which complicates
 *         reasoning and testing.</li>
 *     <li>Adding failure detection and logging increases operational complexity.</li>
 * </ul>
 */
@Deprecated
@RequiredArgsConstructor
public class FallbackRefreshTokenManager implements RefreshTokenManager {

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
