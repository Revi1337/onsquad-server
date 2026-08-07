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
}
