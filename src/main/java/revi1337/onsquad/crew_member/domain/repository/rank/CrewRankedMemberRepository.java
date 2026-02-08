package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.model.CrewRankedMemberDetail;

public interface CrewRankedMemberRepository {

    List<CrewRankedMember> findAll();

    List<CrewRankedMember> findAllByCrewId(Long crewId);

    List<CrewRankedMemberDetail> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit);

    boolean exists();

    void insertBatch(List<CrewRankedMember> rankedMembers);

    void deleteAllInBatch();

    void truncate();

}
