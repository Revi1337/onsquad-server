package revi1337.onsquad.auth.token.application;

import static revi1337.onsquad.auth.token.domain.error.TokenErrorCode.NOT_FOUND_REFRESH;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.token.domain.error.TokenException;
import revi1337.onsquad.auth.token.domain.model.AccessToken;
import revi1337.onsquad.auth.token.domain.model.ClaimsParser;
import revi1337.onsquad.auth.token.domain.model.JsonWebToken;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.auth.token.infrastructure.TokenProperties;
import revi1337.onsquad.auth.token.infrastructure.TokenProperties.TokenAttributes;
import revi1337.onsquad.member.application.dto.MemberSummary;

@Component
public class JsonWebTokenManager {

    private final TokenProperties tokenProperties;
    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;
    private final RefreshTokenStorage refreshTokenStorage;

    public JsonWebTokenManager(
            TokenProperties tokenProperties,
            JsonWebTokenProvider jsonWebTokenProvider,
            JsonWebTokenEvaluator jsonWebTokenEvaluator,
            @Qualifier("redisRefreshTokenStorage") RefreshTokenStorage refreshTokenStorage
    ) {
        this.tokenProperties = tokenProperties;
        this.jsonWebTokenProvider = jsonWebTokenProvider;
        this.jsonWebTokenEvaluator = jsonWebTokenEvaluator;
        this.refreshTokenStorage = refreshTokenStorage;
    }

    public JsonWebToken issueJsonWebToken(MemberSummary summary, Instant now) {
        AccessToken accessToken = issueAccessToken(summary, now);
        RefreshToken refreshToken = issueAndStoreRefreshToken(summary, now);

        return JsonWebToken.create(accessToken, refreshToken);
    }

    public ClaimsParser verifyAccessToken(String accessTokenValue) {
        TokenAttributes tokenAttributes = tokenProperties.accessToken().attributes();

        return jsonWebTokenEvaluator.verifyAccessToken(accessTokenValue, tokenAttributes.secretKey());
    }

    public ClaimsParser verifyRefreshToken(String refreshTokenValue) {
        TokenAttributes tokenAttributes = tokenProperties.refreshToken().attributes();
        ClaimsParser claimsParser = jsonWebTokenEvaluator.verifyRefreshToken(refreshTokenValue, tokenAttributes.secretKey());
        Long memberId = claimsParser.parseIdentity();
        Optional<RefreshToken> optionalRefreshToken = refreshTokenStorage.findTokenBy(memberId);
        if (optionalRefreshToken.isEmpty() || !optionalRefreshToken.get().value().equals(refreshTokenValue)) {
            throw new TokenException.NotFoundRefresh(NOT_FOUND_REFRESH);
        }

        return claimsParser;
    }

    private AccessToken issueAccessToken(MemberSummary summary, Instant now) {
        TokenAttributes tokenAttributes = tokenProperties.accessToken().attributes();
        Map<String, Object> claims = Map.of(
                ClaimsParser.IDENTITY_CLAIM, summary.id(),
                ClaimsParser.EMAIL_CLAIM, summary.email(),
                ClaimsParser.USERTYPE_CLAIM, summary.userType()
        );

        return jsonWebTokenProvider.generateAccessToken(ClaimsParser.ACCESS_TOKEN_SUBJECT, claims, tokenAttributes, now);
    }

    private RefreshToken issueAndStoreRefreshToken(MemberSummary summary, Instant now) {
        TokenAttributes tokenAttributes = tokenProperties.refreshToken().attributes();
        Map<String, Long> tokenClaims = Collections.singletonMap(ClaimsParser.IDENTITY_CLAIM, summary.id());
        RefreshToken refreshToken = jsonWebTokenProvider.generateRefreshToken(ClaimsParser.REFRESH_TOKEN_SUBJECT, tokenClaims, tokenAttributes, now);
        refreshTokenStorage.saveToken(summary.id(), refreshToken, tokenAttributes.expired());

        return refreshToken;
    }
}
