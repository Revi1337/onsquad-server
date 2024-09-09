package revi1337.onsquad.crew_participant.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Repository
public class CrewParticipantJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertCrewParticipant(Long crewId, Long memberId, LocalDateTime now) {
        String sql = "INSERT INTO crew_participant (crew_id, member_id, request_at)" +
                " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE request_at = ?";

        jdbcTemplate.update(sql, crewId, memberId, now, now);
    }
}
