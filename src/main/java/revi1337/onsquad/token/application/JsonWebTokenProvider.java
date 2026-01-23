package revi1337.onsquad.token.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.token.config.TokenProperties;
import revi1337.onsquad.token.config.TokenProperties.AccessTokenAttributes;
import revi1337.onsquad.token.config.TokenProperties.RefreshTokenAttributes;
import revi1337.onsquad.token.config.TokenProperties.TokenAttributes;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.RefreshToken;

@Component
@RequiredArgsConstructor
public class JsonWebTokenProvider {

    private final TokenProperties tokenProperties;
    private final OnsquadProperties onsquadProperties;

    public AccessToken generateAccessToken(String subject, Map<String, ?> extraClaims) {
        AccessTokenAttributes accessTokenAttributes = tokenProperties.accessTokenAttributes();
        return new AccessToken(
                buildToken(
                        subject,
                        extraClaims,
                        accessTokenAttributes.tokenAttributes()
                )
        );
    }

    public RefreshToken generateRefreshToken(String subject, Map<String, ?> extraClaims) {
        RefreshTokenAttributes refreshTokenAttributes = tokenProperties.refreshTokenAttributes();
        return new RefreshToken(
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
                .claim(Claims.ID, UUID.randomUUID().toString())
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
