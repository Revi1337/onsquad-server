package revi1337.onsquad.auth.verification.infrastructure.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

@Component
@RequiredArgsConstructor
public class RdbVerificationCodeStorage implements VerificationCodeStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration expireDuration) {
        long expiredAt = getExpectExpiredTime(expireDuration);
        String sql = """
                INSERT INTO verification_code (email, code, status, expired_at) \
                VALUES (:email, :code, :status, :expiredAt) \
                ON DUPLICATE KEY UPDATE code = :code, status = :status, expired_at = :expiredAt \
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("code", code)
                .addValue("status", status.name())
                .addValue("expiredAt", expiredAt);

        jdbcTemplate.update(sql, params);
        return expiredAt;
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        String sql = "SELECT COUNT(*) FROM verification_code WHERE email = :email AND code = :code AND expired_at > :now";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("code", code)
                .addValue("now", Instant.now().toEpochMilli());

        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus status, Duration expireDuration) {
        String sql = "UPDATE verification_code SET status = :status, expired_at = :expiredAt WHERE email = :email";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("status", status.name())
                .addValue("expiredAt", getExpectExpiredTime(expireDuration));

        int updated = jdbcTemplate.update(sql, params);
        return updated > 0;
    }

    @Override
    public boolean markVerificationStatusAsSuccess(String email, String authCode, Duration expireDuration) {
        String sql = """
                UPDATE verification_code \
                SET status = :successStatus, expired_at = :newExpiredAt \
                WHERE email = :email AND code = :authCode AND expired_at > :now AND status != :successStatus \
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("authCode", authCode)
                .addValue("successStatus", VerificationStatus.SUCCESS.name())
                .addValue("newExpiredAt", getExpectExpiredTime(expireDuration))
                .addValue("now", Instant.now().toEpochMilli());

        int updated = jdbcTemplate.update(sql, params);
        return updated > 0;
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus status) {
        String sql = "SELECT status FROM verification_code WHERE email = :email";

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("email", email);

        try {
            String currentStatus = jdbcTemplate.queryForObject(sql, params, String.class);
            return Objects.equals(currentStatus, status.name());
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public int removeExpiredCodes(long baseTimeMillis) {
        String sql = "DELETE FROM verification_code WHERE expired_at < :baseTime";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("baseTime", baseTimeMillis);

        return jdbcTemplate.update(sql, params);
    }

    private long getExpectExpiredTime(Duration duration) {
        return Instant.now().plus(duration).toEpochMilli();
    }
}
