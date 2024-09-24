package revi1337.onsquad.crew_member.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CrewMemberJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * For more information, visit <a href="https://www.h2database.com/html/functions-window.html">this link</a>.
     * @param crewId
     * @return
     */
    public List<Top5CrewMemberDomainDto> findTopNCrewMembers(Long crewId, int fetchSize) {
        String sql =
            "SELECT rank, mem_id, mem_nickname, mem_mbti, mem_join_time, counter " +
            "FROM (" +
                    "SELECT DENSE_RANK() OVER(ORDER BY counter, mem_join_time DESC) AS rank, mem_id, mem_nickname, mem_mbti, mem_join_time, counter " +
                    "FROM(" +
                    "        SELECT mem_id, mem_nickname, mem_mbti, mem_join_time, COUNT(mem_id) AS counter " +
                    "        FROM (" +
                    "                SELECT " +
                    "                       member.id AS mem_id, " +
                    "                       member.nickname AS mem_nickname, " +
                    "                       member.mbti AS mem_mbti, " +
                    "                       crew_member.participate_at AS mem_join_time " +
                    "                FROM squad_comment " +
                    "                INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id AND crew_member.crew_id = :crewId " +
                    "                INNER JOIN member ON crew_member.member_id = member.id " +

                    "                UNION ALL " +
                    "                SELECT " +
                    "                        member.id, " +
                    "                        member.nickname, " +
                    "                        member.mbti, " +
                    "                        crew_member.participate_at " +
                    "                FROM squad " +
                    "                INNER JOIN crew_member ON squad.crew_member_id = crew_member.id AND squad.crew_id = :crewId " +
                    "                INNER JOIN member ON crew_member.member_id = member.id " +
                    "        )" +
                    "        GROUP BY mem_id " +
                    ") " +
                    "ORDER BY rank" +
            ") AS result " +
            "WHERE result.rank <= :fetchSize";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("crewId", crewId)
                .addValue("fetchSize", fetchSize);

        return jdbcTemplate.query(sql, sqlParameterSource, top5RowMapper());
    }

    private RowMapper<Top5CrewMemberDomainDto> top5RowMapper() {
        return (rs, rowNum) -> new Top5CrewMemberDomainDto(
                rs.getInt("rank"),
                rs.getInt("counter"),
                rs.getLong("mem_id"),
                new Nickname(rs.getString("mem_nickname")),
                rs.getObject("mem_mbti", Mbti.class),
                rs.getObject("mem_join_time", LocalDateTime.class)
        );
    }
}
