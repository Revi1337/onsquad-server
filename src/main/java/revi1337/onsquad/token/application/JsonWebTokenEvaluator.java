package revi1337.onsquad.token.application;

import static revi1337.onsquad.token.domain.error.TokenErrorCode.INVALID_TOKEN_SIGNATURE;
import static revi1337.onsquad.token.domain.error.TokenErrorCode.TOKEN_EXPIRED;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import revi1337.onsquad.token.domain.error.TokenException;
import revi1337.onsquad.token.domain.model.ClaimsParser;

@Component
public class JsonWebTokenEvaluator {

    public ClaimsParser verifyAccessToken(String accessToken, String secretKey) {
        return new ClaimsParser(verifyToken(accessToken, secretKey));
    }

    public ClaimsParser verifyRefreshToken(String refreshToken, String secretKey) {
        return new ClaimsParser(verifyToken(refreshToken, secretKey));
    }

    private Claims verifyToken(String token, String secretKey) {
        try {
            return extractAllClaims(token, secretKey);
        } catch (SignatureException e) {
            throw new TokenException.InvalidTokenSignature(INVALID_TOKEN_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new TokenException.TokenExpired(TOKEN_EXPIRED);
        }
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
