package revi1337.onsquad.auth.token.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;

@Import(RdbRefreshTokenStorage.class)
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
class RdbRefreshTokenStorageTest {

    @Autowired
    private RdbRefreshTokenStorage refreshTokenStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS refresh_token");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS refresh_token (
                    member_id   BIGINT       PRIMARY KEY,
                    token_value VARCHAR(512) NOT NULL,
                    expired_at  BIGINT       NOT NULL
                );
                """);
    }

    @Nested
    @DisplayName("리프레시 토큰 저장")
    class SaveToken {

        @Test
        @DisplayName("새로운 토큰을 저장하면 DB에 레코드가 생성된다.")
        void saveNewToken() {
            String tokenValue = "refresh-token-value";
            Date expiredAt = new Date(Instant.now().plus(Duration.ofDays(1)).toEpochMilli());
            RefreshToken refreshToken = new RefreshToken(memberId, tokenValue, expiredAt);

            refreshTokenStorage.saveToken(memberId, refreshToken, Duration.ofDays(1));

            Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM refresh_token WHERE member_id = ?", memberId);
            assertSoftly(softly -> {
                softly.assertThat(result.get("member_id")).isEqualTo(memberId);
                softly.assertThat(result.get("token_value")).isEqualTo(tokenValue);
                softly.assertThat(result.get("expired_at")).isEqualTo(expiredAt.getTime());
            });
        }

        @Test
        @DisplayName("이미 존재하는 memberId로 저장하면 기존 토큰 값이 업데이트된다. (Upsert)")
        void updateExistingToken() {
            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, "old-token", new Date()), Duration.ofMinutes(5));
            String newTokenValue = "new-token";
            Date newExpiredAt = new Date(Instant.now().plus(Duration.ofHours(1)).toEpochMilli());

            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, newTokenValue, newExpiredAt), Duration.ofHours(1));

            Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM refresh_token WHERE member_id = ?", memberId);
            assertThat(result.get("token_value")).isEqualTo(newTokenValue);
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 조회")
    class FindTokenBy {

        @Test
        @DisplayName("유효한 토큰이 존재하면 Optional에 담아 반환한다.")
        void returnTokenWhenExistsAndNotExpired() {
            String tokenValue = "valid-token";
            Date expiredAt = new Date(Instant.now().plus(Duration.ofHours(1)).toEpochMilli());
            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, tokenValue, expiredAt), Duration.ofHours(1));

            Optional<RefreshToken> result = refreshTokenStorage.findTokenBy(memberId);

            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(tokenValue);
        }

        @Test
        @DisplayName("토큰이 만료되었거나 존재하지 않으면 빈 Optional을 반환한다.")
        void returnEmptyWhenExpiredOrNotFound() {
            Date expiredAt = new Date(Instant.now().minus(Duration.ofHours(1)).toEpochMilli());
            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, "expired", expiredAt), Duration.ofHours(-1));

            Optional<RefreshToken> result = refreshTokenStorage.findTokenBy(memberId);
            Optional<RefreshToken> notFoundResult = refreshTokenStorage.findTokenBy(999L);

            assertSoftly(softly -> {
                softly.assertThat(result).isEmpty();
                softly.assertThat(notFoundResult).isEmpty();
            });
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 삭제")
    class DeleteTokens {

        @Test
        @DisplayName("특정 회원의 토큰을 삭제한다.")
        void deleteByMemberId() {
            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, "token", new Date()), Duration.ofMinutes(1));

            refreshTokenStorage.deleteTokenBy(memberId);

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refresh_token WHERE member_id = ?", Integer.class, memberId);
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("모든 토큰을 삭제한다.")
        void deleteAllTokens() {
            refreshTokenStorage.saveToken(1L, new RefreshToken(1L, "t1", new Date()), Duration.ofMinutes(1));
            refreshTokenStorage.saveToken(2L, new RefreshToken(2L, "t2", new Date()), Duration.ofMinutes(1));

            refreshTokenStorage.deleteAll();

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refresh_token", Integer.class);
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("만료된 토큰 대량 삭제")
    class RemoveExpiredTokens {

        @Test
        @DisplayName("기준 시간보다 이전에 만료된 토큰들만 삭제한다.")
        void deleteOnlyExpiredTokens() {
            long now = Instant.now().toEpochMilli();
            refreshTokenStorage.saveToken(1L, new RefreshToken(1L, "exp", new Date(now - 600000)), Duration.ofMinutes(-10)); // 10분전 만료
            refreshTokenStorage.saveToken(2L, new RefreshToken(2L, "valid", new Date(now + 3600000)), Duration.ofHours(1)); // 1시간 후 만료

            int deletedCount = refreshTokenStorage.removeExpiredTokens(now);

            assertSoftly(softly -> {
                softly.assertThat(deletedCount).isEqualTo(1);
                softly.assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refresh_token", Integer.class)).isEqualTo(1);
                softly.assertThat(jdbcTemplate.queryForObject("SELECT member_id FROM refresh_token", Long.class)).isEqualTo(2L);
            });
        }
    }
}
