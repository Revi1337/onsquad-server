package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.response.CrewMembersWithCountResponse;
import revi1337.onsquad.crew_member.application.response.MyParticipantResponse;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.result.CrewMemberWithCountResult;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewMemberRepository crewMemberRepository;

    public CrewMembersWithCountResponse fetchParticipants(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewMemberAccessPolicy.ensureReadParticipantsAccessible(crewMember);
        List<CrewMemberWithCountResult> results = crewMemberRepository.fetchParticipantsWithCountByCrewId(crewId, pageable);

        return CrewMembersWithCountResponse.from(results);
    }

    public List<MyParticipantResponse> fetchMyParticipatingCrews(Long memberId) {
        return crewMemberRepository.fetchParticipantCrews(memberId).stream()
                .map(MyParticipantResponse::from)
                .toList();
    }
}
