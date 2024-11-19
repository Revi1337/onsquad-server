package revi1337.onsquad.crew_participant.domain;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;

@RequiredArgsConstructor
@Repository
public class CrewParticipantJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CrewParticipant upsertCrewParticipant(Long crewId, Long memberId, LocalDateTime now) {
        String sql = "INSERT INTO crew_participant (crew_id, member_id, request_at)" +
                " VALUES (:crewId, :memberId, :now) ON DUPLICATE KEY UPDATE request_at = :now";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("crewId", crewId)
                .addValue("memberId", memberId)
                .addValue("now", now);

        int influenced = namedParameterJdbcTemplate.update(sql, mapSqlParameterSource, generatedKeyHolder);

        return new CrewParticipant(
                generatedKeyHolder.getKey().longValue(),
                Crew.builder().id(crewId).build(),
                Member.builder().id(memberId).build(),
                now
        );
    }
}
