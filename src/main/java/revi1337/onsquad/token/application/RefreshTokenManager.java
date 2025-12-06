package revi1337.onsquad.token.application;

import java.util.Optional;
import revi1337.onsquad.token.domain.model.RefreshToken;

public interface RefreshTokenManager {

    void saveTokenFor(Long memberId, RefreshToken refreshToken);

    Optional<RefreshToken> findTokenBy(Long memberId);

    void deleteTokenBy(Long memberId);

}
