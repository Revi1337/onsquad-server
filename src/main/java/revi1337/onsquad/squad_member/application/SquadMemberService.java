package revi1337.onsquad.squad_member.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
import revi1337.onsquad.squad_member.application.dto.SquadInMembersDto;
import revi1337.onsquad.squad_member.application.dto.SquadMembersWithSquadDto;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberService {

    private final SquadMemberRepository squadMemberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<EnrolledSquadDto> fetchAllJoinedSquads(Long memberId) {
        return squadMemberRepository.fetchAllJoinedSquadsByMemberId(memberId).stream()
                .map(EnrolledSquadDto::from)
                .toList();
    }

    public SquadMembersWithSquadDto findSquadWithMembers(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return SquadMembersWithSquadDto.from(
                squadMemberRepository.getMembersWithSquad(memberId, crewId, squadId)
        );
    }

    public SquadInMembersDto fetchMembersInSquad(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember ignored = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());

        return SquadInMembersDto.from(
                memberId,
                squadMemberRepository.fetchAllWithCrewAndCategoriesBySquadId(crewMember.getId(), squadId)
        );
    }
}
