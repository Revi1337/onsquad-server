package revi1337.onsquad.squad_request.domain.repository;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for upserting Squad requests using JDBC.
 * <p>
 * This class is no longer in use.
 * </p>
 */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
@Repository
public class SquadRequestJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertSquadParticipant(Long squadId, Long memberId, LocalDateTime now) {
        String sql = "INSERT INTO squad_request (squad_id, member_id, request_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE request_at = ?";
        jdbcTemplate.update(sql, squadId, memberId, now, now);
    }
}
