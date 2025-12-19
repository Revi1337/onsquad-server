package revi1337.onsquad.crew.domain.repository.top;

import java.time.LocalDate;
import java.util.List;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;
import revi1337.onsquad.crew.domain.result.Top5CrewMemberResult;

public interface CrewTopMemberRepository {

    List<Top5CrewMemberResult> fetchAggregatedTopMembers(LocalDate from, LocalDate to, Integer rankLimit);

    boolean exists();

    void deleteAllInBatch();

    void batchInsert(List<CrewTopMember> crewTopCaches);

}
