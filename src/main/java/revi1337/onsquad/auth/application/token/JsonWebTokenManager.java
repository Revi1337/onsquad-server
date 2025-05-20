package revi1337.onsquad.auth.application.token;

import java.util.Collections;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.model.token.AccessToken;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.member.application.dto.MemberSummary;

@RequiredArgsConstructor
@Service
public class JsonWebTokenManager {

    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final RefreshTokenManager expiringMapRefreshTokenManager;

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
        expiringMapRefreshTokenManager.saveTokenFor(memberId, refreshToken);
    }
}
