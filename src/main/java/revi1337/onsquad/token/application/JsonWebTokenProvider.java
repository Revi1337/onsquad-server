package revi1337.onsquad.token.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.ClaimsParser;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.infrastructure.TokenProperties.TokenAttributes;

@Component
@RequiredArgsConstructor
public class JsonWebTokenProvider {

    private final OnsquadProperties onsquadProperties;

    public AccessToken generateAccessToken(String subject, Map<String, ?> extraClaims, TokenAttributes accessTokenAttributes, Instant now) {
        Date issuedAt = Date.from(now);
        Date expiredAt = Date.from(now.plus(accessTokenAttributes.expired()));
        String accessToken = buildToken(subject, extraClaims, issuedAt, expiredAt, accessTokenAttributes.secretKey());

        return new AccessToken(accessToken, expiredAt);
    }

    public RefreshToken generateRefreshToken(String subject, Map<String, ?> extraClaims, TokenAttributes refreshtokenAttributes, Instant now) {
        Date issuedAt = Date.from(now);
        Date expiredAt = Date.from(now.plus(refreshtokenAttributes.expired()));
        String refreshToken = buildToken(subject, extraClaims, issuedAt, expiredAt, refreshtokenAttributes.secretKey());

        Long identifier = Optional.ofNullable(extraClaims.get(ClaimsParser.IDENTITY_CLAIM))
                .map(String::valueOf)
                .map(Long::valueOf)
                .orElse(null);

        return new RefreshToken(identifier, refreshToken, expiredAt);
    }

    private String buildToken(String subject, Map<String, ?> extraClaims, Date issuedAt, Date expiredAt, String secretKey) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .claim(Claims.ID, UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuer(onsquadProperties.getApplicationName().toUpperCase())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }
}
