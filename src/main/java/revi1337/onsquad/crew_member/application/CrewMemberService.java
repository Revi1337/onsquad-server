package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.response.CrewMembersWithCountResponse;
import revi1337.onsquad.crew_member.application.response.MyParticipantResponse;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.CrewMemberWithCountResult;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrewMemberService {

    private final CrewMemberAccessor crewMemberAccessor;

    public CrewMembersWithCountResponse fetchParticipants(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureReadParticipantsAccessible(crewMember);
        List<CrewMemberWithCountResult> results = crewMemberAccessor.fetchParticipantsWithCountByCrewId(crewId, pageable);

        return CrewMembersWithCountResponse.from(results);
    }

    public List<MyParticipantResponse> fetchMyParticipatingCrews(Long memberId) {
        return crewMemberAccessor.fetchParticipantCrews(memberId).stream()
                .map(MyParticipantResponse::from)
                .toList();
    }
}
