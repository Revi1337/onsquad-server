package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

@Repository
@RequiredArgsConstructor
public class CrewActivityLogJdbcRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public List<CrewRankerCandidate> aggregateRankedMembersGivenActivityWeight(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        String sql = """
                    \n
                    SELECT
                        ranked_activities.crew_id AS crew_id,
                        ranked_activities.mem_id AS mem_id,
                        ranked_activities.mem_nickname AS mem_nickname,
                        ranked_activities.mem_mbti AS mem_mbti,
                        ranked_activities.last_activity_time AS mem_last_activity_time,
                        ranked_activities.total_score AS score,
                        ranked_activities.ranks AS ranks
                    FROM (
                        SELECT
                            crew_id, mem_id, mem_nickname, mem_mbti, last_activity_time, total_score,
                            DENSE_RANK() OVER (PARTITION BY crew_id ORDER BY total_score DESC, last_activity_time DESC) AS ranks
                        FROM (
                            SELECT
                                crew_activity_log.crew_id AS crew_id,
                                m.id AS mem_id,
                                m.nickname AS mem_nickname,
                                m.mbti AS mem_mbti,
                                MAX(crew_activity_log.created_at) AS last_activity_time,
                                SUM(crew_activity_log.weight) AS total_score
                            FROM crew_activity_log
                            INNER JOIN member m ON m.id = crew_activity_log.member_id
                            WHERE crew_activity_log.created_at BETWEEN :from AND :to
                            GROUP BY crew_activity_log.crew_id, m.id, m.nickname, m.mbti
                        ) AS aggregated_activities
                    ) AS ranked_activities
                    WHERE ranks <= :rankLimit
                    ORDER BY crew_id, ranks;
                """;

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("rankLimit", rankLimit);

        return namedJdbcTemplate.query(sql, sqlParameterSource, crewRankerCandidateMapper());
    }

    private RowMapper<CrewRankerCandidate> crewRankerCandidateMapper() {
        return (rs, rowNum) -> new CrewRankerCandidate(
                rs.getLong("crew_id"),
                rs.getInt("ranks"),
                rs.getLong("score"),
                rs.getLong("mem_id"),
                rs.getString("mem_nickname"),
                rs.getString("mem_mbti"),
                rs.getObject("mem_last_activity_time", LocalDateTime.class)
        );
    }
}
