package revi1337.onsquad.auth.token.infrastructure.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.token.application.RefreshTokenStorage;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;

@Component
@RequiredArgsConstructor
public class RdbRefreshTokenStorage implements RefreshTokenStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration) {
        String sql = """
                INSERT INTO refresh_token (member_id, token_value, expired_at) \
                VALUES (:memberId, :tokenValue, :expiredAt) \
                ON DUPLICATE KEY UPDATE token_value = :tokenValue, expired_at = :expiredAt \
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("tokenValue", refreshToken.value())
                .addValue("expiredAt", refreshToken.expiredAt().getTime());

        jdbcTemplate.update(sql, params);
        return refreshToken.expiredAt().getTime();
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        String sql = "SELECT member_id, token_value, expired_at FROM refresh_token WHERE member_id = :memberId AND expired_at > :now";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("now", Instant.now().toEpochMilli());

        try {
            RefreshToken token = jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> new RefreshToken(
                    rs.getLong("member_id"),
                    rs.getString("token_value"),
                    new Date(rs.getLong("expired_at"))
            ));
            return Optional.ofNullable(token);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        String sql = "DELETE FROM refresh_token WHERE member_id = :memberId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM refresh_token";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    public int removeExpiredTokens(long baseTimeMillis) {
        String sql = "DELETE FROM refresh_token WHERE expired_at < :baseTime";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("baseTime", baseTimeMillis);

        return jdbcTemplate.update(sql, params);
    }
}
