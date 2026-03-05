package revi1337.onsquad.auth.token.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.auth.token.domain.model.RefreshTokens;

class ExpiringMapRefreshTokenStorageTest {

    private final ExpiringMapRefreshTokenStorage storage = new ExpiringMapRefreshTokenStorage();
    private final ExpiringMap<String, RefreshToken> refreshStore = (ExpiringMap<String, RefreshToken>) ReflectionTestUtils.getField(storage, "refreshStore");

    private final Long memberId = 1L;
    private final String key = "onsquad:refresh-token:user:1";

    @BeforeEach
    void setUp() {
        refreshStore.clear();
    }

    @Nested
    @DisplayName("리프레시 토큰 저장")
    class SaveToken {

        @Test
        @DisplayName("토큰을 저장하면 memberId 기반의 키로 RefreshToken 객체가 Store에 기록된다")
        void recordRefreshTokenObject() {
            String tokenValue = "refresh-token-value";
            Duration duration = Duration.ofMinutes(30);
            Date expiredAt = new Date(Instant.now().plus(duration).toEpochMilli());
            RefreshToken refreshToken = new RefreshToken(memberId, tokenValue, expiredAt);

            storage.saveToken(memberId, refreshToken, duration);

            assertSoftly(softly -> {
                RefreshToken result = refreshStore.get(key);
                softly.assertThat(result).isNotNull();
                softly.assertThat(result.identifier()).isEqualTo(memberId);
                softly.assertThat(result.value()).isEqualTo(tokenValue);
                softly.assertThat(result.expiredAt()).isEqualTo(expiredAt);
            });
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 조회")
    class FindTokenBy {

        @Test
        @DisplayName("저장된 토큰이 존재하면 Optional에 담아 반환한다")
        void returnTokenWhenExists() {
            RefreshToken refreshToken = new RefreshToken(memberId, "token", new Date());
            storage.saveToken(memberId, refreshToken, Duration.ofMinutes(5));

            Optional<RefreshToken> result = storage.findTokenBy(memberId);

            assertSoftly(softly -> {
                softly.assertThat(result).isPresent();
                softly.assertThat(result.get().value()).isEqualTo("token");
            });
        }

        @Test
        @DisplayName("존재하지 않는 memberId로 조회하면 빈 Optional을 반환한다")
        void returnEmptyWhenNotFound() {
            Optional<RefreshToken> result = storage.findTokenBy(999L);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 삭제")
    class DeleteTokens {

        @Test
        @DisplayName("특정 회원의 토큰을 메모리 저장소에서 즉시 제거한다")
        void deleteByMemberId() {
            storage.saveToken(memberId, new RefreshToken(memberId, "token", new Date()), Duration.ofMinutes(1));

            storage.deleteTokenBy(memberId);

            assertThat(refreshStore.containsKey(key)).isFalse();
        }

        @Test
        @DisplayName("저장소의 모든 토큰 데이터를 초기화한다")
        void deleteAll() {
            storage.saveToken(1L, new RefreshToken(1L, "t1", new Date()), Duration.ofMinutes(1));
            storage.saveToken(2L, new RefreshToken(2L, "t2", new Date()), Duration.ofMinutes(1));

            storage.deleteAll();

            assertThat(refreshStore.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("전체 토큰 데이터 수집")
    class GetTokens {

        @Test
        @DisplayName("현재 메모리에 살아있는 모든 리프레시 토큰 객체를 수집하여 반환한다")
        void collectAllLiveTokens() {
            storage.saveToken(1L, new RefreshToken(1L, "t1", new Date()), Duration.ofMinutes(5));
            storage.saveToken(2L, new RefreshToken(2L, "t2", new Date()), Duration.ofMinutes(5));

            RefreshTokens result = storage.getTokens();

            assertThat(result.refreshTokens()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("데이터 자동 만료")
    class DataExpiration {

        @Test
        @DisplayName("설정된 유효 시간이 지나면 ExpiringMap 정책에 의해 데이터가 자동 삭제된다")
        void autoRemoveAfterExpiration() throws InterruptedException {
            storage.saveToken(memberId, new RefreshToken(memberId, "token", new Date()), Duration.ofMillis(100));

            Thread.sleep(200);

            assertThat(refreshStore.containsKey(key)).isFalse();
        }
    }
}
