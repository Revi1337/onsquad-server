package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

@Repository
@RequiredArgsConstructor
public class CrewActivityScoreRepositoryImpl implements CrewActivityScoreRepository {

    private final CrewActivityScoreJpaRepository crewActivityScoreJpaRepository;
    private final CrewActivityScoreJdbcRepository crewActivityScoreJdbcRepository;

    @Override
    public void upsertScore(Long crewId, Long memberId, int weight, LocalDateTime lastActivityAt) {
        crewActivityScoreJdbcRepository.upsertScore(crewId, memberId, weight, lastActivityAt);
    }

    @Override
    public List<CrewRankerCandidate> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        return crewActivityScoreJdbcRepository.aggregateRankedMembersGivenActivityWeight(from, to, rankLimit);
    }

    @Override
    public void deleteByLastActivityAtBetween(LocalDateTime from, LocalDateTime to) {
        crewActivityScoreJpaRepository.deleteByLastActivityAtBetween(from, to);
    }
}
