package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;

@Repository
@RequiredArgsConstructor
public class CrewRankerRepositoryImpl implements CrewRankerRepository {

    private final CrewLeaderboardProperties crewLeaderboardProperties;
    private final CrewRankerJpaRepository crewRankerJpaRepository;
    private final CrewRankerJdbcRepository crewRankerJdbcRepository;

    @Override
    public List<CrewRanker> findAll() {
        return crewRankerJpaRepository.findAll();
    }

    @Override
    public List<CrewRanker> findAllByCrewId(Long crewId) {
        return crewRankerJpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(crewId, crewLeaderboardProperties.rankLimit());
    }

    @Override
    public List<CrewRankerDetail> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        return crewRankerJdbcRepository.aggregateRankedMembersGivenActivityWeight(from, to, rankLimit);
    }

    @Override
    public boolean exists() {
        return crewRankerJpaRepository.existsBy();
    }

    @Override
    public void deleteAllInBatch() {
        crewRankerJpaRepository.deleteAllInBatch();
    }

    @Override
    public void insertBatch(List<CrewRanker> rankedMembers) {
        crewRankerJdbcRepository.insertBatch(rankedMembers);
    }

    @Override
    public void truncate() {
        crewRankerJdbcRepository.truncate();
    }
}
