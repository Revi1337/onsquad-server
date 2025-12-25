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
        String sql = "INSERT INTO crew_top_member(crew_id, member_id, nickname, mbti, participate_at, contribute, ranks) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(
                sql,
                crewTopMembers,
                crewTopMembers.size(),
                (ps, crewTopCache) -> {
                    ps.setLong(1, crewTopCache.getCrewId());
                    ps.setLong(2, crewTopCache.getMemberId());
                    ps.setString(3, crewTopCache.getNickname());
                    ps.setString(4, crewTopCache.getMbti());
                    ps.setObject(5, crewTopCache.getParticipateAt());
                    ps.setInt(6, crewTopCache.getContribute());
                    ps.setInt(7, crewTopCache.getRanks());
                }
        );
    }

    /**
     * For more information, visit <a href="https://www.h2database.com/html/functions-window.html">this link</a>.
     *
     * @param from
     * @param to
     * @param rankLimit
     */
    public List<Top5CrewMemberResult> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit) {
        String sql = """
                    \n
                    SELECT
                        crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter, ranks
                    FROM (
                        SELECT
                            crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter,
                            DENSE_RANK() OVER(PARTITION BY crew_id ORDER BY counter DESC, mem_join_time) AS ranks
                        FROM (
                            SELECT DISTINCT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter
                            FROM (
                                SELECT
                                    cm.crew_id AS crew_id,
                                    m.id AS mem_id,
                                    m.nickname AS mem_nickname,
                                    m.mbti AS mem_mbti,
                                    cm.participate_at AS mem_join_time,
                                    COUNT(*) OVER (PARTITION BY cm.crew_id, m.id) AS counter
                                FROM (
                                    SELECT
                                        sc.member_id AS member_id, 
                                        sc.created_at AS created_at
                                    FROM squad_comment sc
                                    WHERE sc.created_at BETWEEN :from AND :to
                                    UNION ALL
                                    SELECT
                                        s.member_id AS member_id, 
                                        s.created_at AS created_at
                                    FROM squad s
                                ) AS activities
                                INNER JOIN member m ON activities.member_id = m.id
                                INNER JOIN crew_member cm ON m.id = cm.member_id
                            ) AS union_table
                        ) AS distinct_table
                    ) AS rank_table
                    WHERE ranks <= :rankLimit
                    ORDER BY crew_id, ranks
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
                rs.getInt("counter"),
                rs.getLong("mem_id"),
                rs.getString("mem_nickname"),
                rs.getString("mem_mbti"),
                rs.getObject("mem_join_time", LocalDateTime.class)
        );
    }
}
