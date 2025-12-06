package revi1337.onsquad.token.domain.repository;

import java.time.Duration;
import java.util.Optional;
import revi1337.onsquad.token.domain.model.RefreshToken;

public interface TokenRepository {

    void save(RefreshToken refreshToken, Long memberId, Duration expired);

    Optional<RefreshToken> findBy(Long memberId);

    void deleteBy(Long memberId);

    void deleteAll();

}
