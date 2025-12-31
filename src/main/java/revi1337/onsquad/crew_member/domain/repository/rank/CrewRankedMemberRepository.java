package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

public interface CrewRankedMemberRepository {

    List<CrewRankedMember> findAllByCrewId(Long crewId);

    List<CrewRankedMemberResult> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit);

    boolean exists();

    void deleteAllInBatch();

    void insertBatch(List<CrewRankedMember> rankedMembers);

}
