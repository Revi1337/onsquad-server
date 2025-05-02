package revi1337.onsquad.backup.crew.domain;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewTopMemberRepositoryImpl implements CrewTopMemberRepository {

    private final CrewTopMemberJpaRepository crewTopMemberJpaRepository;
    private final CrewTopMemberJdbcRepository crewTopMemberJdbcRepository;

    @Override
    public List<Top5CrewMemberDomainDto> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit) {
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
    public void batchInsert(List<CrewTopMember> crewTopCaches) {
        crewTopMemberJdbcRepository.batchInsertCrewTop(crewTopCaches);
    }
}
