package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDate;
import java.util.List;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

public interface CrewRankedMemberRepository {

    List<CrewRankedMember> findAllByCrewId(Long crewId);

    List<CrewRankedMemberResult> fetchAggregatedRankedMembers(LocalDate from, LocalDate to, Integer rankLimit);

    boolean exists();

    void deleteAllInBatch();

    void insertBatch(List<CrewRankedMember> rankedMembers);

}
