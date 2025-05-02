package revi1337.onsquad.backup.crew.domain;

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
import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewTopMemberJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchInsertCrewTop(List<CrewTopMember> crewTopMembers) {
        String sql =
                "INSERT INTO crew_top_member(crew_id, member_id, nickname, mbti, participate_at, contribute, ranks) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
    public List<Top5CrewMemberDomainDto> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit) {
        String sql = """
                        \n
                        SELECT
                            crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter, ranks
                        FROM (
                            SELECT
                                crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter,
                                DENSE_RANK() OVER(PARTITION BY crew_id ORDER BY counter DESC, mem_join_time) AS ranks
                            FROM (
                                SELECT DISTINCT
                                        crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter
                                FROM (
                                    SELECT
                                        crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time,
                                        COUNT(*) OVER (PARTITION BY crew_id, mem_id) AS counter
                                    FROM (
                                        SELECT
                                            crew_member.crew_id AS crew_id,
                                            member.id AS mem_id,
                                            member.nickname AS mem_nickname,
                                            member.mbti AS mem_mbti,
                                            crew_member.participate_at AS mem_join_time
                                        FROM squad_comment
                                        INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id AND squad_comment.created_at BETWEEN :from AND :to
                                        INNER JOIN member ON crew_member.member_id = member.id
                                    
                                        UNION ALL
                                        SELECT
                                            crew_member.crew_id,
                                            member.id,
                                            member.nickname,
                                            member.mbti,
                                            crew_member.participate_at
                                        FROM squad
                                        INNER JOIN crew_member ON squad.crew_member_id = crew_member.id
                                        INNER JOIN member ON crew_member.member_id = member.id
                                    ) AS union_table
                                ) AS count_table
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

    /**
     * @param crewId
     * @see #fetchAggregatedTopMembers
     * @deprecated This API is no longer used.
     */
    @Deprecated
    public List<Top5CrewMemberDomainDto> fetchAggregatedTopMembers(Long crewId, int fetchSize) {
        String sql = """
                        \n
                        SELECT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter, ranks
                        FROM (
                            SELECT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter,
                                    DENSE_RANK() OVER(ORDER BY counter DESC, mem_join_time) AS ranks
                            FROM (
                                SELECT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, COUNT(mem_id) AS counter
                                FROM (
                                    SELECT
                                        crew_member.crew_id AS crew_id,
                                        member.id AS mem_id,
                                        member.nickname AS mem_nickname,
                                        member.mbti AS mem_mbti,
                                        crew_member.participate_at AS mem_join_time
                                    FROM squad_comment
                                    INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id AND crew_member.crew_id = :crewId
                                    INNER JOIN member ON crew_member.member_id = member.id
                        
                                    UNION ALL
                                    SELECT
                                        crew_member.crew_id,
                                        member.id,
                                        member.nickname,
                                        member.mbti,
                                        crew_member.participate_at
                                    FROM squad
                                    INNER JOIN crew_member ON squad.crew_member_id = crew_member.id AND squad.crew_id = :crewId
                                    INNER JOIN member ON crew_member.member_id = member.id
                                ) AS union_table
                                GROUP BY mem_id
                            ) AS count_table
                            ORDER BY ranks
                        ) AS result
                        WHERE result.ranks <= :fetchSize;
                """;

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("crewId", crewId)
                .addValue("fetchSize", fetchSize);

        return namedJdbcTemplate.query(sql, sqlParameterSource, top5RowMapper());
    }

    private RowMapper<Top5CrewMemberDomainDto> top5RowMapper() {
        return (rs, rowNum) -> new Top5CrewMemberDomainDto(
                rs.getLong("crew_id"),
                rs.getInt("ranks"),
                rs.getInt("counter"),
                rs.getLong("mem_id"),
                rs.getString("mem_nickname"),
                rs.getString("mem_mbti"),
                rs.getObject("mem_join_time", LocalDateTime.class)
        );
    }

    private RowMapper<Top5CrewMemberDomainDto> crewTop5RowMapper() {
        return (rs, rowNum) -> new Top5CrewMemberDomainDto(
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
