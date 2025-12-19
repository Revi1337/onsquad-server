package revi1337.onsquad.squad_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_member.application.response.EnrolledSquadResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberQueryService {

    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadMemberRepository squadMemberRepository;

    public List<EnrolledSquadResponse> fetchAllJoinedSquads(Long memberId) {
        return squadMemberRepository.fetchAllJoinedSquadsByMemberId(memberId).stream()
                .map(EnrolledSquadResponse::from)
                .toList();
    }

    public List<SquadMemberResponse> fetchAllBySquadId(Long memberId, Long squadId) {
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadMemberAccessPolicy.ensureReadParticipantsAccessible(squadMember);

        return squadMemberRepository.fetchAllBySquadId(squadId).stream()
                .map(domainDto -> SquadMemberResponse.from(memberId, domainDto))
                .toList();
    }
}
