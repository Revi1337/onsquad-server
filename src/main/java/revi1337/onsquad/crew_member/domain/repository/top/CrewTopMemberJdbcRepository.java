package revi1337.onsquad.crew_member.domain.repository.top;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;
import revi1337.onsquad.crew_member.domain.result.Top5CrewMemberResult;

@RequiredArgsConstructor
@Repository
public class CrewTopMemberJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void insertBatch(List<CrewTopMember> crewTopMembers) {
        String sql = "INSERT INTO crew_top_member(crew_id, member_id, nickname, mbti, last_activity_time, score, ranks) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(
                sql,
                crewTopMembers,
                crewTopMembers.size(),
                (ps, crewTopCache) -> {
                    ps.setLong(1, crewTopCache.getCrewId());
                    ps.setLong(2, crewTopCache.getMemberId());
                    ps.setString(3, crewTopCache.getNickname());
                    ps.setString(4, crewTopCache.getMbti());
                    ps.setObject(5, crewTopCache.getLastActivityTime());
                    ps.setInt(6, crewTopCache.getScore());
                    ps.setInt(7, crewTopCache.getRanks());
                }
        );
    }

    /**
     * For more information, visit <a href="https://www.h2database.com/html/functions-window.html">this link</a>.
     *
     * @see #aggregateRankedMembersGivenActivityWeight(LocalDate, LocalDate, Integer)
     * @deprecated
     */
    @Deprecated
    public List<Top5CrewMemberResult> aggregateRankedMembersByActivityOccurrence(LocalDate from, LocalDate to, Integer rankLimit) {
        String sql = """
                    \n
                    SELECT
                        ranked_activities.crew_id AS crew_id,
                        ranked_activities.mem_id AS mem_id,
                        ranked_activities.mem_nickname AS mem_nickname,
                        ranked_activities.mem_mbti AS mem_mbti,
                        ranked_activities.last_activity_time AS mem_last_activity_time,
                        ranked_activities.counter AS score,
                        ranked_activities.ranks AS ranks
                    FROM (
                        SELECT
                            crew_id, mem_id, mem_nickname, mem_mbti, last_activity_time, counter,
                            DENSE_RANK() OVER (PARTITION BY crew_id ORDER BY counter DESC, last_activity_time DESC) AS ranks
                        FROM (
                            SELECT DISTINCT
                                raw_activities.crew_id AS crew_id,
                                m.id AS mem_id,
                                m.nickname AS mem_nickname,
                                m.mbti AS mem_mbti,
                                MAX(raw_activities.created_at) OVER (PARTITION BY raw_activities.crew_id, m.id) AS last_activity_time,
                                COUNT(*) OVER (PARTITION BY raw_activities.crew_id, m.id) AS counter
                            FROM (
                                -- crew participant
                                SELECT cm.crew_id, cm.member_id, cm.participate_at AS created_at
                                FROM crew_member cm
                                WHERE cm.participate_at BETWEEN :from AND :to
                                UNION ALL
                                -- squad create
                                SELECT s.crew_id, s.member_id, s.created_at AS created_at
                                FROM squad s
                                WHERE s.created_at BETWEEN :from AND :to
                                UNION ALL
                                -- squad participant
                                SELECT s.crew_id, sm.member_id, sm.participate_at AS created_at
                                FROM squad_member sm
                                INNER JOIN squad s ON s.id = sm.squad_id
                                WHERE sm.participate_at BETWEEN :from AND :to
                                UNION ALL
                                -- squad comment create
                                SELECT s.crew_id, sc.member_id, sc.created_at AS created_at
                                FROM squad_comment sc
                                INNER JOIN squad s ON s.id = sc.squad_id
                                WHERE sc.created_at BETWEEN :from AND :to
                            ) AS raw_activities
                            INNER JOIN member m ON m.id = raw_activities.member_id
                        ) AS aggregated_activities
                    ) AS ranked_activities
                    WHERE ranks <= :rankLimit
                    ORDER BY crew_id, ranks;
                """;

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("from", Date.valueOf(from))
                .addValue("to", Date.valueOf(to))
                .addValue("rankLimit", rankLimit);

        return namedJdbcTemplate.query(sql, sqlParameterSource, crewTop5RowMapper());
    }

    public List<Top5CrewMemberResult> aggregateRankedMembersGivenActivityWeight(LocalDate from, LocalDate to, Integer rankLimit) {
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
                            SELECT DISTINCT
                                raw_activities.crew_id AS crew_id,
                                m.id AS mem_id,
                                m.nickname AS mem_nickname,
                                m.mbti AS mem_mbti,
                                MAX(raw_activities.created_at) OVER (PARTITION BY raw_activities.crew_id, m.id) AS last_activity_time,
                                SUM(raw_activities.point) OVER (PARTITION BY raw_activities.crew_id, m.id) AS total_score
                            FROM (
                                -- crew participant (Weight: 5)
                                SELECT cm.crew_id, cm.member_id, cm.participate_at AS created_at, 5 AS point
                                FROM crew_member cm
                                WHERE cm.participate_at BETWEEN :from AND :to
                                UNION ALL
                                -- squad create (Weight: 10)
                                SELECT s.crew_id, s.member_id, s.created_at AS created_at, 10 AS point
                                FROM squad s
                                WHERE s.created_at BETWEEN :from AND :to
                                UNION ALL
                                -- squad participant (Weight: 3)
                                SELECT s.crew_id, sm.member_id, sm.participate_at AS created_at, 3 AS point
                                FROM squad_member sm
                                INNER JOIN squad s ON s.id = sm.squad_id
                                WHERE sm.participate_at BETWEEN :from AND :to
                                UNION ALL
                                -- squad comment create (Weight: 1)
                                SELECT s.crew_id, sc.member_id, sc.created_at AS created_at, 1 AS point
                                FROM squad_comment sc
                                INNER JOIN squad s ON s.id = sc.squad_id
                                WHERE sc.created_at BETWEEN :from AND :to
                            ) AS raw_activities
                            INNER JOIN member m ON m.id = raw_activities.member_id
                        ) AS aggregated_activities
                    ) AS ranked_activities
                    WHERE ranks <= :rankLimit
                    ORDER BY crew_id, ranks;
                """;

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("from", Date.valueOf(from))
                .addValue("to", Date.valueOf(to))
                .addValue("rankLimit", rankLimit);

        return namedJdbcTemplate.query(sql, sqlParameterSource, crewTop5RowMapper());
    }

    private RowMapper<Top5CrewMemberResult> crewTop5RowMapper() {
        return (rs, rowNum) -> new Top5CrewMemberResult(
                rs.getLong("crew_id"),
                rs.getInt("ranks"),
                rs.getInt("score"),
                rs.getLong("mem_id"),
                rs.getString("mem_nickname"),
                rs.getString("mem_mbti"),
                rs.getObject("mem_last_activity_time", LocalDateTime.class)
        );
    }
}
