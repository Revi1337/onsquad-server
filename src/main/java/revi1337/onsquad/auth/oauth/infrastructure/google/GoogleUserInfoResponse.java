package revi1337.onsquad.auth.oauth.infrastructure.google;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigInteger;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleUserInfoResponse(
        BigInteger id,
        String email,
        boolean verifiedEmail,
        String name,
        String givenName,
        String familyName,
        String picture
) {

}
