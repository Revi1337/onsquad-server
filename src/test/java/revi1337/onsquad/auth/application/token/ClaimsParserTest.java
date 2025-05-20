package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_USER_TYPE;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.vo.UserType;

class ClaimsParserTest {

    @Test
    @DisplayName("ClaimsParser 생성에 성공한다.")
    void constructor() {
        ClaimsParser parser = new ClaimsParser(generateClaims());

        assertThat(parser).isNotNull();
    }

    @Test
    @DisplayName("Identity Claim 추출에 성공한다.")
    void parseIdentity() {
        ClaimsParser parser = new ClaimsParser(generateClaims());

        Long identity = parser.parseIdentity();

        assertThat(identity).isEqualTo(1L);
    }

    @Test
    @DisplayName("Email Claim 추출에 성공한다.")
    void parseEmail() {
        ClaimsParser parser = new ClaimsParser(generateClaims());

        String email = parser.parseEmail();

        assertThat(email).isEqualTo(REVI_EMAIL_VALUE);
    }

    @Test
    @DisplayName("userType Claim 추출에 성공한다.")
    void parseUserType() {
        ClaimsParser parser = new ClaimsParser(generateClaims());

        UserType userType = parser.parseUserType();

        assertThat(userType).isSameAs(REVI_USER_TYPE);
    }

    private Claims generateClaims() {
        MemberSummary summary = new MemberSummary(1L, REVI_EMAIL_VALUE, null, REVI_USER_TYPE);
        return Jwts.claims(new HashMap<>() {
            {
                put(ClaimsParser.IDENTITY_CLAIM, summary.id());
                put(ClaimsParser.EMAIL_CLAIM, summary.email());
                put(ClaimsParser.USERTYPE_CLAIM, summary.userType().name());
            }
        });
    }
}