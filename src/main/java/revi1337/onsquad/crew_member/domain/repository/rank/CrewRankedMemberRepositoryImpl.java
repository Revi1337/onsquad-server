package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

@Repository
@RequiredArgsConstructor
public class CrewRankedMemberRepositoryImpl implements CrewRankedMemberRepository {

    private final CrewRankedMemberProperties crewRankedMemberProperties;
    private final CrewRankedMemberJpaRepository crewRankedMemberJpaRepository;
    private final CrewRankedMemberJdbcRepository crewRankedMemberJdbcRepository;

    @Override
    public List<CrewRankedMember> findAll() {
        return crewRankedMemberJpaRepository.findAll();
    }

    @Override
    public List<CrewRankedMember> findAllByCrewId(Long crewId) {
        return crewRankedMemberJpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(crewId, crewRankedMemberProperties.rankLimit());
    }

    @Override
    public List<CrewRankedMemberResult> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        return crewRankedMemberJdbcRepository.aggregateRankedMembersGivenActivityWeight(from, to, rankLimit);
    }

    @Override
    public boolean exists() {
        return crewRankedMemberJpaRepository.existsBy();
    }

    @Override
    public void deleteAllInBatch() {
        crewRankedMemberJpaRepository.deleteAllInBatch();
    }

    @Override
    public void insertBatch(List<CrewRankedMember> rankedMembers) {
        crewRankedMemberJdbcRepository.insertBatch(rankedMembers);
    }

    @Override
    public void truncate() {
        crewRankedMemberJdbcRepository.truncate();
    }
}
