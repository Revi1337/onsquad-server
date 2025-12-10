package revi1337.onsquad.crew.domain.repository.top;

import java.time.LocalDate;
import java.util.List;
import revi1337.onsquad.crew.domain.dto.top.Top5CrewMemberDomainDto;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;

public interface CrewTopMemberRepository {

    List<Top5CrewMemberDomainDto> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit);

    boolean exists();

    void deleteAllInBatch();

    void batchInsert(List<CrewTopMember> crewTopCaches);

}
