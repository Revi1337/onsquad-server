package revi1337.onsquad.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.domain.redis.RedisTokenRepository;
import revi1337.onsquad.auth.domain.vo.RefreshToken;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RedisRefreshTokenManager implements RefreshTokenManager {

    private final RedisTokenRepository redisTokenRepository;
    private final TokenProperties tokenProperties;

    @Override
    public void storeTemporaryToken(RefreshToken refreshToken, Long id) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        redisTokenRepository.storeTemporaryRefreshToken(refreshToken, id, expired);
    }

    @Override
    public Optional<Long> findTemporaryToken(RefreshToken refreshToken) {
        return redisTokenRepository.retrieveTemporaryRefreshToken(refreshToken);
    }

    @Override
    public void removeTemporaryToken(RefreshToken refreshToken) {
        redisTokenRepository.deleteTemporaryRefreshToken(refreshToken);
    }
}
