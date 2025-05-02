package revi1337.onsquad.backup.crew.domain;

import java.time.LocalDate;
import java.util.List;
import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;

public interface CrewTopMemberRepository {

    List<Top5CrewMemberDomainDto> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit);

    boolean exists();

    void deleteAllInBatch();

    void batchInsert(List<CrewTopMember> crewTopCaches);

}
