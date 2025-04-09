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
    public void storeTemporaryToken(RefreshToken refreshToken, Long memberId) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        redisTokenOperation.storeTemporaryRefreshToken(refreshToken, memberId, expired);
    }

    @Override
    public Optional<RefreshToken> findTemporaryToken(Long memberId) {
        return redisTokenOperation.retrieveTemporaryRefreshToken(memberId);
    }

    @Override
    public void removeTemporaryToken(Long memberId) {
        redisTokenOperation.deleteTemporaryRefreshToken(memberId);
    }
}
