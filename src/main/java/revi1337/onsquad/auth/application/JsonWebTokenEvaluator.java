package revi1337.onsquad.auth.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.error.exception.AuthTokenException;

import java.security.Key;
import java.util.function.Function;

import static revi1337.onsquad.auth.config.properties.TokenProperties.*;
import static revi1337.onsquad.auth.error.TokenErrorCode.*;

@Component
public class JsonWebTokenEvaluator {

    private AccessTokenAttributes accessTokenAttributes;
    private RefreshTokenAttributes refreshTokenAttributes;

    public JsonWebTokenEvaluator(TokenProperties tokenProperties) {
        this.accessTokenAttributes = tokenProperties.accessTokenAttributes();
        this.refreshTokenAttributes = tokenProperties.refreshTokenAttributes();
    }

    public Claims verifyAccessToken(String accessToken) {
        String accessTokenSecretKey = accessTokenAttributes.tokenAttributes().secretKey();
        return verifyToken(accessToken, accessTokenSecretKey);
    }

    public Claims verifyRefreshToken(String refreshToken) {
        String refreshTokenSecretKey = refreshTokenAttributes.tokenAttributes().secretKey();
        return verifyToken(refreshToken, refreshTokenSecretKey);
    }

    public <V extends Claims, T> T extractSpecificClaim(V claims, Function<V, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }

    private Claims verifyToken(String token, String secretKey) {
        try {
            return extractAllClaims(token, secretKey);
        } catch (SignatureException e) {
            throw new AuthTokenException.InvalidTokenSignature(INVALID_TOKEN_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new AuthTokenException.TokenExpired(TOKEN_EXPIRED);
        }
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String keyType) {
        return Keys.hmacShaKeyFor(keyType.getBytes());
    }
}