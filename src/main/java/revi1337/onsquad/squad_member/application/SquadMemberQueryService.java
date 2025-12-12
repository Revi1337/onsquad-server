package revi1337.onsquad.squad_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberQueryService {

    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadMemberRepository squadMemberRepository;

    public List<EnrolledSquadDto> fetchAllJoinedSquads(Long memberId) {
        return squadMemberRepository.fetchAllJoinedSquadsByMemberId(memberId).stream()
                .map(EnrolledSquadDto::from)
                .toList();
    }

    public List<SquadMemberDto> fetchAllBySquadId(Long memberId, Long squadId) {
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadMemberAccessPolicy.ensureReadParticipantsAccessible(squadMember);
        return squadMemberRepository.fetchAllBySquadId(squadId).stream()
                .map(domainDto -> SquadMemberDto.from(memberId, domainDto))
                .toList();
    }
}
