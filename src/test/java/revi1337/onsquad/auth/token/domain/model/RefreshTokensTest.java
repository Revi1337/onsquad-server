package revi1337.onsquad.auth.token.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokensTest {

    @Test
    @DisplayName("리스트가 비어있으면 isEmpty 는 true 를 반환한다.")
    void returnTrueWhenListIsEmpty() {
        RefreshTokens refreshTokens = new RefreshTokens(Collections.emptyList());

        assertThat(refreshTokens.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("유효한 토큰과 만료된 토큰이 섞여있을 때, 유효한 토큰만 필터링하여 반환한다.")
    void extractOnlyAvailableTokens() {
        Instant now = Instant.now();
        RefreshToken validToken = new RefreshToken(1L, "valid-token", new Date(now.plus(Duration.ofHours(1)).toEpochMilli()));
        RefreshToken expiredToken = new RefreshToken(2L, "expired-token", new Date(now.minus(Duration.ofHours(1)).toEpochMilli()));
        RefreshToken nullExpiredToken = new RefreshToken("null-token");
        RefreshTokens refreshTokens = new RefreshTokens(List.of(validToken, expiredToken, nullExpiredToken));

        List<RefreshToken> result = refreshTokens.extractAvailableBefore(now);

        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result).containsExactly(validToken);
            softly.assertThat(result.get(0).value()).isEqualTo("valid-token");
        });
    }

    @Test
    @DisplayName("모든 토큰이 만료되었다면 빈 리스트를 반환한다.")
    void returnEmptyListWhenAllTokensExpired() {
        Instant now = Instant.now();
        RefreshToken expired1 = new RefreshToken(1L, "e1", new Date(now.minusSeconds(10).toEpochMilli()));
        RefreshToken expired2 = new RefreshToken(2L, "e2", new Date(now.minusSeconds(20).toEpochMilli()));
        RefreshTokens refreshTokens = new RefreshTokens(List.of(expired1, expired2));

        List<RefreshToken> result = refreshTokens.extractAvailableBefore(now);

        assertThat(result).isEmpty();
    }
}
