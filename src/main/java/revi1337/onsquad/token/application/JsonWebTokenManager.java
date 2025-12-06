package revi1337.onsquad.token.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.RefreshToken;

@RequiredArgsConstructor
@Component
public class JsonWebTokenManager {

    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final RefreshTokenManager redisRefreshTokenManager;

    public AccessToken generateAccessToken(MemberSummary summary) {
        return jsonWebTokenProvider.generateAccessToken(
                ClaimsParser.ACCESS_TOKEN_SUBJECT,
                new HashMap<>() {
                    {
                        put(ClaimsParser.IDENTITY_CLAIM, summary.id());
                        put(ClaimsParser.EMAIL_CLAIM, summary.email());
                        put(ClaimsParser.USERTYPE_CLAIM, summary.userType());
                    }
                }
        );
    }

    public RefreshToken generateRefreshToken(Long memberId) {
        return jsonWebTokenProvider.generateRefreshToken(
                ClaimsParser.REFRESH_TOKEN_SUBJECT,
                Collections.singletonMap(ClaimsParser.IDENTITY_CLAIM, memberId)
        );
    }

    public void storeRefreshTokenFor(Long memberId, RefreshToken refreshToken) {
        redisRefreshTokenManager.saveTokenFor(memberId, refreshToken);
    }

    public Optional<RefreshToken> findRefreshTokenBy(Long memberId) {
        return redisRefreshTokenManager.findTokenBy(memberId);
    }
}
