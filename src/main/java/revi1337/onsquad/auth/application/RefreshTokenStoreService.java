package revi1337.onsquad.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.config.TokenProperties;
import revi1337.onsquad.auth.domain.redis.RedisTokenRepository;
import revi1337.onsquad.auth.dto.response.RefreshToken;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RefreshTokenStoreService {

    private final RedisTokenRepository redisTokenRepository;
    private final TokenProperties tokenProperties;

    public void storeTemporaryToken(RefreshToken refreshToken, Long id) {
        Duration expired = tokenProperties.refreshTokenAttributes().tokenAttributes().expired();
        redisTokenRepository.storeTemporaryRefreshToken(refreshToken, id, expired);
    }
}
