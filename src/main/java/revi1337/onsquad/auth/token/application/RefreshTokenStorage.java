package revi1337.onsquad.auth.token.application;

import java.time.Duration;
import java.util.Optional;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;

public interface RefreshTokenStorage {

    long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration);

    Optional<RefreshToken> findTokenBy(Long memberId);

    void deleteTokenBy(Long memberId);

    void deleteAll();

}
