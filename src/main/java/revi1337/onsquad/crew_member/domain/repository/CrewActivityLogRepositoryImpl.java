package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityLog;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

@Repository
@RequiredArgsConstructor
public class CrewActivityLogRepositoryImpl implements CrewActivityLogRepository {

    private final CrewActivityLogJpaRepository crewActivityLogJpaRepository;
    private final CrewActivityLogJdbcRepository crewActivityLogJdbcRepository;

    @Override
    public CrewActivityLog save(CrewActivityLog crewActivityLog) {
        return crewActivityLogJpaRepository.save(crewActivityLog);
    }

    @Override
    public List<CrewRankerCandidate> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        return crewActivityLogJdbcRepository.aggregateRankedMembersGivenActivityWeight(from, to, rankLimit);
    }

    @Override
    public void deleteByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
        crewActivityLogJpaRepository.deleteByCreatedAtBetween(from, to);
    }
}
