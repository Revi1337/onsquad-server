package revi1337.onsquad.crew_participant.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
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
                generatedKeyHolder.getKey() == null ? null : generatedKeyHolder.getKey().longValue(),
                prepareCrew(crewId),
                prepareMember(memberId),
                now
        );
    }

    private Member prepareMember(Long memberId) {
        try {
            Constructor<Member> defaultConstructor = ReflectionUtils.accessibleConstructor(Member.class);
            defaultConstructor.setAccessible(true);
            Member member = defaultConstructor.newInstance();
            Field idField = member.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            ReflectionUtils.setField(idField, member, memberId);
            return member;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Crew prepareCrew(Long crewId) {
        try {
            Constructor<Crew> defaultConstructor = ReflectionUtils.accessibleConstructor(Crew.class);
            defaultConstructor.setAccessible(true);
            Crew crew = defaultConstructor.newInstance();
            Field idField = crew.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            ReflectionUtils.setField(idField, crew, crewId);
            return crew;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
