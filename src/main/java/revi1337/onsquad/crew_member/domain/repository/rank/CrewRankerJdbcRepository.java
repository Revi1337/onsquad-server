package revi1337.onsquad.crew_member.domain.repository.rank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

@Repository
@RequiredArgsConstructor
public class CrewRankerJdbcRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void insertBatch(List<CrewRankerCandidate> candidates) {
        String sql = "INSERT INTO crew_ranker(crew_id, member_id, nickname, mbti, last_activity_time, score, ranks) VALUES (?, ?, ?, ?, ?, ?, ?)";
        namedJdbcTemplate.getJdbcOperations().batchUpdate(
                sql,
                candidates,
                candidates.size(),
                (ps, candidate) -> {
                    ps.setLong(1, candidate.crewId());
                    ps.setLong(2, candidate.memberId());
                    ps.setString(3, candidate.nickname());
                    ps.setString(4, candidate.mbti());
                    ps.setObject(5, candidate.lastActivityTime());
                    ps.setLong(6, candidate.score());
                    ps.setInt(7, candidate.rank());
                }
        );
    }

    // RENAME TABLE/CREATE TABLE/DROP TABLE은 MySQL에서 DDL이라 암묵적 커밋(implicit commit)을 유발한다.
    // 따라서 이 메서드를 호출하는 트랜잭션은 이 지점에서 사실상 커밋되며, 이후 같은 메서드 내 다른 작업은 별개의 커밋 단위가 된다.
    public void swapSnapshot(List<CrewRankerCandidate> candidates) {
        String shadowTable = "crew_ranker_shadow";
        String oldTable = "crew_ranker_old";

        namedJdbcTemplate.getJdbcOperations().execute("DROP TABLE IF EXISTS " + shadowTable);
        namedJdbcTemplate.getJdbcOperations().execute("CREATE TABLE " + shadowTable + " LIKE crew_ranker");

        String sql = "INSERT INTO " + shadowTable + "(crew_id, member_id, nickname, mbti, last_activity_time, score, ranks) VALUES (?, ?, ?, ?, ?, ?, ?)";
        namedJdbcTemplate.getJdbcOperations().batchUpdate(
                sql,
                candidates,
                candidates.size(),
                (ps, candidate) -> {
                    ps.setLong(1, candidate.crewId());
                    ps.setLong(2, candidate.memberId());
                    ps.setString(3, candidate.nickname());
                    ps.setString(4, candidate.mbti());
                    ps.setObject(5, candidate.lastActivityTime());
                    ps.setLong(6, candidate.score());
                    ps.setInt(7, candidate.rank());
                }
        );

        namedJdbcTemplate.getJdbcOperations().execute(
                "RENAME TABLE crew_ranker TO " + oldTable + ", " + shadowTable + " TO crew_ranker"
        );
        namedJdbcTemplate.getJdbcOperations().execute("DROP TABLE " + oldTable);
    }
}
