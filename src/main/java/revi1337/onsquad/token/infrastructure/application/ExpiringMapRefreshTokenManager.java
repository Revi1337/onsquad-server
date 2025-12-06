package revi1337.onsquad.token.infrastructure.application;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.token.application.RefreshTokenManager;
import revi1337.onsquad.token.config.TokenProperties;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.infrastructure.repository.ExpiringMapTokenRepository;

/**
 * @see RedisRefreshTokenManager
 * @deprecated This implementation is deprecated because it relies on an in-memory ExpiringMap, which leads to several critical consistency and reliability
 * issues:*
 *
 * <p><b>Volatile storage:</b> All tokens are lost when the application restarts,
 * making it unsuitable for any environment that requires persistence or durability.</p>
 *
 * <p><b>No horizontal scalability:</b> Because tokens exist only inside a single JVM instance,
 * running multiple application servers results in inconsistent token availability across nodes.</p>
 *
 * <p><b>Session inconsistency:</b> Users may be logged out unexpectedly when load balancers
 * route traffic to different nodes that do not share the same in-memory token state.</p>
 *
 * <p><b>Operational unpredictability:</b> Behavior becomes non-deterministic under scaling,
 * rolling deployments, auto-restarts, or container rescheduling, which breaks reliable authentication flows.</p>
 *
 * <p>For these reasons, a distributed and persistent token store such as Redis should be preferred.</p>
 */
@Deprecated
@RequiredArgsConstructor
@Component
public class ExpiringMapRefreshTokenManager implements RefreshTokenManager {

    private final TokenProperties tokenProperties;
    private final ExpiringMapTokenRepository tokenRepository;

    @Override
    public void saveTokenFor(Long memberId, RefreshToken refreshToken) {
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
