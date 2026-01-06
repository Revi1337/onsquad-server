package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

@RequiredArgsConstructor
@Repository
public class CrewRankedMemberRepositoryImpl implements CrewRankedMemberRepository {

    private final CrewRankedMemberJpaRepository crewRankedMemberJpaRepository;
    private final CrewRankedMemberJdbcRepository crewRankedMemberJdbcRepository;

    @Override
    public List<CrewRankedMember> findAll() {
        return crewRankedMemberJpaRepository.findAll();
    }

    @Override
    public List<CrewRankedMember> findAllByCrewId(Long crewId) {
        return crewRankedMemberJpaRepository.findAllByCrewId(crewId);
    }

    @Override
    public List<CrewRankedMemberResult> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        return crewRankedMemberJdbcRepository.aggregateRankedMembersGivenActivityWeight(from, to, rankLimit);
    }

    @Override
    public boolean exists() {
        return crewRankedMemberJpaRepository.exists();
    }

    @Override
    public void deleteAllInBatch() {
        crewRankedMemberJpaRepository.deleteAllInBatch();
    }

    @Override
    public void truncate() {
        crewRankedMemberJdbcRepository.truncate();
    }

    @Override
    public void insertBatch(List<CrewRankedMember> rankedMembers) {
        crewRankedMemberJdbcRepository.insertBatch(rankedMembers);
    }
}
