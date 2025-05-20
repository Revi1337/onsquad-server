package revi1337.onsquad.auth.application.token;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.auth.repository.token.RedisHashTokenRepository;

@Deprecated
@RequiredArgsConstructor
@Service
public class RedisRefreshTokenManager implements RefreshTokenManager {

    private final RedisHashTokenRepository redisTokenOperation;
    private final TokenProperties tokenProperties;

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
