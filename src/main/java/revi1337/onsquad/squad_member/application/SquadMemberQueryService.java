package revi1337.onsquad.squad_member.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberQueryService {

    private final SquadMemberRepository squadMemberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<EnrolledSquadDto> fetchAllJoinedSquads(Long memberId) {
        return squadMemberRepository.fetchAllJoinedSquadsByMemberId(memberId).stream()
                .map(EnrolledSquadDto::from)
                .toList();
    }

    public List<SquadMemberDto> fetchAllBySquadId(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isLeader()) {
            return new ArrayList<>(); // TODO 여긱 구현해야 함.
        }

        return squadMemberRepository.fetchAllBySquadId(squadId).stream()
                .map(domainDto -> SquadMemberDto.from(memberId, domainDto))
                .toList();
    }
}
