package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.response.CrewMemberResponse;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewMemberRepository crewMemberRepository;

    public List<CrewMemberResponse> fetchCrewMembers(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewMemberAccessPolicy.ensureReadParticipantsAccessible(crewMember);

        return crewMemberRepository.findManagedCrewMembersByCrewId(crewId, pageable).stream()
                .map(CrewMemberResponse::from)
                .toList();
    }
}
