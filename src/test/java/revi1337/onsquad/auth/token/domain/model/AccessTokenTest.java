package revi1337.onsquad.auth.token.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccessTokenTest {

    @Test
    @DisplayName("AccessToken 생성 시 만료 시간을 지정하지 않으면 null로 설정된다.")
    void createAccessTokenWithOnlyValue() {
        String tokenValue = "access-token-string";

        AccessToken accessToken = new AccessToken(tokenValue);

        assertSoftly(softly -> {
            softly.assertThat(accessToken.value()).isEqualTo(tokenValue);
            softly.assertThat(accessToken.expiredAt()).isNull();
        });
    }

    @Test
    @DisplayName("토큰 값(value)이 동일하면 동등한 객체로 판단한다.")
    void equalsAndHashCodeByValue() {
        String tokenValue = "same-token-value";
        Date now = new Date();
        AccessToken token1 = new AccessToken(tokenValue, now);
        AccessToken token2 = new AccessToken(tokenValue, new Date(now.getTime() + 1000));

        assertSoftly(softly -> {
            softly.assertThat(token1).isEqualTo(token2);
            softly.assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
        });
    }

    @Test
    @DisplayName("토큰 값(value)이 다르면 서로 다른 객체로 판단한다.")
    void notEqualsByValue() {
        AccessToken token1 = new AccessToken("token-1");
        AccessToken token2 = new AccessToken("token-2");

        assertThat(token1).isNotEqualTo(token2);
    }
}
