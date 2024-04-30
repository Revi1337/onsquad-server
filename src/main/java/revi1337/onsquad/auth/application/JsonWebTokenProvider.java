package revi1337.onsquad.auth.application;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.config.TokenProperties;
import revi1337.onsquad.auth.dto.response.AccessToken;
import revi1337.onsquad.auth.dto.response.RefreshToken;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static revi1337.onsquad.auth.config.TokenProperties.*;

@RequiredArgsConstructor
@Component
public class JsonWebTokenProvider {

    private final TokenProperties tokenProperties;
    private final OnsquadProperties onsquadProperties;

    public AccessToken generateAccessToken(String subject, Map<String, ?> extraClaims) {
        AccessTokenAttributes accessTokenAttributes = tokenProperties.accessTokenAttributes();
        return AccessToken.of(
                buildToken(
                        subject,
                        extraClaims,
                        accessTokenAttributes.tokenAttributes()
                )
        );
    }

    public RefreshToken generateRefreshToken(String subject, Map<String, ?> extraClaims) {
        RefreshTokenAttributes refreshTokenAttributes = tokenProperties.refreshTokenAttributes();
        return RefreshToken.of(
                buildToken(
                        subject,
                        extraClaims,
                        refreshTokenAttributes.tokenAttributes()
                )
        );
    }

    private String buildToken(String subject, Map<String, ?> extraClaims, TokenAttributes tokenAttributes) {
        return buildToken(
                subject,
                extraClaims,
                tokenAttributes.expired(),
                getSigningKey(tokenAttributes.secretKey())
        );
    }

    private String buildToken(String subject, Map<String, ?> extraClaims, Duration expiration, Key key) {
        Instant currentTime = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuer(onsquadProperties.getApplicationName().toUpperCase())
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(Date.from(currentTime.plus(expiration)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSigningKey(String keyType) {
        return Keys.hmacShaKeyFor(keyType.getBytes(StandardCharsets.UTF_8));
    }
}
