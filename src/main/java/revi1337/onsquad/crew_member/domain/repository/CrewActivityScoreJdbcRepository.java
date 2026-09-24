package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.model.CrewActivityScoreSnapshot;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

@Repository
@RequiredArgsConstructor
public class CrewActivityScoreJdbcRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void upsertScore(Long crewId, Long memberId, int weight, LocalDateTime lastActivityAt) {
        String sql = """
                INSERT INTO crew_activity_score(crew_id, member_id, weight, last_activity_at)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE weight = weight + VALUES(weight), last_activity_at = GREATEST(last_activity_at, VALUES(last_activity_at))
                """;
        namedJdbcTemplate.getJdbcOperations().update(sql, crewId, memberId, weight, lastActivityAt);
    }

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
                                crew_activity_score.crew_id AS crew_id,
                                m.id AS mem_id,
                                m.nickname AS mem_nickname,
                                m.mbti AS mem_mbti,
                                crew_activity_score.last_activity_at AS last_activity_time,
                                crew_activity_score.weight AS total_score
                            FROM crew_activity_score
                            INNER JOIN member m ON m.id = crew_activity_score.member_id
                            WHERE crew_activity_score.last_activity_at BETWEEN :from AND :to
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

    public List<CrewActivityScoreSnapshot> fetchSnapshot(LocalDateTime from, LocalDateTime to) {
        String sql = "SELECT crew_id, member_id, weight FROM crew_activity_score WHERE last_activity_at BETWEEN :from AND :to";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return namedJdbcTemplate.query(sql, sqlParameterSource, (rs, rowNum) -> new CrewActivityScoreSnapshot(
                rs.getLong("crew_id"),
                rs.getLong("member_id"),
                rs.getInt("weight")
        ));
    }

    public void subtractCountedWeight(List<CrewActivityScoreSnapshot> snapshot) {
        if (snapshot.isEmpty()) {
            return;
        }
        namedJdbcTemplate.getJdbcOperations().execute("""
                CREATE TEMPORARY TABLE tmp_activity_score_snapshot (
                    crew_id   BIGINT NOT NULL,
                    member_id BIGINT NOT NULL,
                    weight    INT    NOT NULL,
                    PRIMARY KEY (crew_id, member_id)
                ) ENGINE = MEMORY
                """);
        try {
            namedJdbcTemplate.getJdbcOperations().batchUpdate(
                    "INSERT INTO tmp_activity_score_snapshot (crew_id, member_id, weight) VALUES (?, ?, ?)",
                    snapshot,
                    snapshot.size(),
                    (ps, row) -> {
                        ps.setLong(1, row.crewId());
                        ps.setLong(2, row.memberId());
                        ps.setInt(3, row.weight());
                    }
            );
            namedJdbcTemplate.getJdbcOperations().update("""
                    UPDATE crew_activity_score cas
                    INNER JOIN tmp_activity_score_snapshot s
                        ON cas.crew_id = s.crew_id AND cas.member_id = s.member_id
                    SET cas.weight = cas.weight - s.weight
                    WHERE cas.weight >= s.weight
                    """);
        } finally {
            namedJdbcTemplate.getJdbcOperations().execute("DROP TEMPORARY TABLE tmp_activity_score_snapshot");
        }
    }

    public void deleteZeroWeightRows() {
        namedJdbcTemplate.getJdbcOperations().update("DELETE FROM crew_activity_score WHERE weight = 0");
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
