package revi1337.onsquad.squad_request.domain.repository;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SquadRequestJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertSquadParticipant(Long squadId, Long crewMemberId, LocalDateTime now) {
        String sql = "INSERT INTO squad_request (squad_id, crew_member_id, request_at)" +
                " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE request_at = ?";

        jdbcTemplate.update(sql, squadId, crewMemberId, now, now);
    }
}
