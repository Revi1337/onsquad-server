package revi1337.onsquad.auth.repository.token;

import java.time.Duration;
import java.util.Optional;
import revi1337.onsquad.auth.application.token.model.RefreshToken;

public interface TokenRepository {

    void save(RefreshToken refreshToken, Long memberId, Duration expired);

    Optional<RefreshToken> findBy(Long memberId);

    void deleteBy(Long memberId);

    void deleteAll();

}
