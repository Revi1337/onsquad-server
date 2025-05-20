package revi1337.onsquad.auth.application.token;

import java.util.Optional;
import revi1337.onsquad.auth.application.token.model.RefreshToken;

public interface RefreshTokenManager {

    void saveTokenFor(Long memberId, RefreshToken refreshToken);

    Optional<RefreshToken> findTokenBy(Long memberId);

    void deleteTokenBy(Long memberId);

}
