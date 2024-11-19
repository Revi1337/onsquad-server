package revi1337.onsquad.backup.crew.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CrewTopCacheJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertCrewTop(List<CrewTopCache> crewTopCaches) {
        String sql = "INSERT INTO crew_top_cache(crew_id, member_id, nickname, mbti, participate_at, counter, ranks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                crewTopCaches,
                crewTopCaches.size(),
                (ps, crewTopCache) -> {
                    ps.setLong(1, crewTopCache.getCrewId());
                    ps.setLong(2, crewTopCache.getMemberId());
                    ps.setString(3, crewTopCache.getNickname());
                    ps.setString(4, crewTopCache.getMbti());
                    ps.setObject(5, crewTopCache.getParticipateAt());
                    ps.setInt(6, crewTopCache.getCounter());
                    ps.setInt(7, crewTopCache.getRanks());
                }
        );
    }
}
