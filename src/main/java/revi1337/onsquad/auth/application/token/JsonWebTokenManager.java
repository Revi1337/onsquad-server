package revi1337.onsquad.auth.application.token;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.member.application.dto.MemberSummary;

@RequiredArgsConstructor
@Component
public class JsonWebTokenManager {

    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final RefreshTokenManager defaultRefreshTokenManager;

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
        defaultRefreshTokenManager.saveTokenFor(memberId, refreshToken);
    }

    public Optional<RefreshToken> findRefreshTokenBy(Long memberId) {
        return defaultRefreshTokenManager.findTokenBy(memberId);
    }
}
