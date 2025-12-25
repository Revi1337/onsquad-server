package revi1337.onsquad.crew_member.domain.repository.top;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;
import revi1337.onsquad.crew_member.domain.result.Top5CrewMemberResult;

@RequiredArgsConstructor
@Repository
public class CrewTopMemberRepositoryImpl implements CrewTopMemberRepository {

    private final CrewTopMemberJpaRepository crewTopMemberJpaRepository;
    private final CrewTopMemberJdbcRepository crewTopMemberJdbcRepository;

    @Override
    public List<Top5CrewMemberResult> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit) {
        return crewTopMemberJdbcRepository.fetchAggregatedTopMembers(from, to, rankLimit);
    }

    @Override
    public boolean exists() {
        return crewTopMemberJpaRepository.exists();
    }

    @Override
    public void deleteAllInBatch() {
        crewTopMemberJpaRepository.deleteAllInBatch();
    }

    @Override
    public void insertBatch(List<CrewTopMember> crewTopCaches) {
        crewTopMemberJdbcRepository.insertBatch(crewTopCaches);
    }
}
