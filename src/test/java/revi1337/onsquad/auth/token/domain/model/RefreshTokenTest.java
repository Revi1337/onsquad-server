package revi1337.onsquad.auth.token.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

    @Test
    @DisplayName("RefreshToken 생성 시 토큰 값만 인자로 주면 identifier와 expiredAt은 null로 설정된다.")
    void createRefreshTokenWithOnlyValue() {
        String tokenValue = "refresh-token-string";

        RefreshToken refreshToken = new RefreshToken(tokenValue);

        assertSoftly(softly -> {
            softly.assertThat(refreshToken.value()).isEqualTo(tokenValue);
            softly.assertThat(refreshToken.identifier()).isNull();
            softly.assertThat(refreshToken.expiredAt()).isNull();
        });
    }

    @Nested
    @DisplayName("토큰 유효성 검증 (isAvailableAt)")
    class IsAvailableAt {

        @Test
        @DisplayName("현재 시간이 만료 시간보다 이전이면 true를 반환한다.")
        void returnTrueWhenNotExpired() {
            Instant now = Instant.now();
            Date future = new Date(now.plusSeconds(60).toEpochMilli());
            RefreshToken refreshToken = new RefreshToken(1L, "token", future);

            boolean result = refreshToken.isAvailableAt(now);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("현재 시간이 만료 시간과 같거나 이후이면 false를 반환한다.")
        void returnFalseWhenExpired() {
            Instant now = Instant.now();
            Date past = new Date(now.minusSeconds(1).toEpochMilli());
            RefreshToken refreshToken = new RefreshToken(1L, "token", past);

            boolean result = refreshToken.isAvailableAt(now);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("만료 시간(expiredAt) 정보가 없으면 false를 반환한다.")
        void returnFalseWhenExpiredAtIsNull() {
            RefreshToken refreshToken = new RefreshToken("token");

            boolean result = refreshToken.isAvailableAt(Instant.now());

            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("토큰 값(value)이 동일하면 identifier나 expiredAt이 달라도 동등한 객체로 판단한다.")
    void equalsAndHashCodeByValue() {
        String tokenValue = "same-token-value";
        RefreshToken token1 = new RefreshToken(1L, tokenValue, new Date());
        RefreshToken token2 = new RefreshToken(2L, tokenValue, new Date(System.currentTimeMillis() + 10000));

        assertSoftly(softly -> {
            softly.assertThat(token1).isEqualTo(token2);
            softly.assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
        });
    }
}
