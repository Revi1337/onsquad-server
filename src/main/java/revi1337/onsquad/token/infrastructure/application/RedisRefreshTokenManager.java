package revi1337.onsquad.token.infrastructure.application;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.token.application.RefreshTokenManager;
import revi1337.onsquad.token.config.TokenProperties;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.infrastructure.repository.RedisHashTokenRepository;

@RequiredArgsConstructor
@Component
public class RedisRefreshTokenManager implements RefreshTokenManager {

    private final TokenProperties tokenProperties;
    private final RedisHashTokenRepository redisTokenOperation;

    @Override
    public void saveTokenFor(Long memberId, RefreshToken refreshToken) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        redisTokenOperation.save(refreshToken, memberId, expired);
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        return redisTokenOperation.findBy(memberId);
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        redisTokenOperation.deleteBy(memberId);
    }
}
