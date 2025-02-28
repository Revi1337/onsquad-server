package revi1337.onsquad.squad_member.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;
import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_IN_SQUAD;

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
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberService {

    private final SquadMemberRepository squadMemberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<EnrolledSquadDto> findEnrolledSquads(Long memberId) {
        return squadMemberRepository.findEnrolledSquads(memberId).stream()
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
        if (!squadMemberRepository.existsBySquadIdAndCrewMemberId(squadId, crewMember.getId())) {
            throw new SquadMemberBusinessException.NotInSquad(NOT_IN_SQUAD);
        }

        return SquadInMembersDto.from(
                memberId,
                squadMemberRepository.fetchAllWithCrewAndCategoriesBySquadId(crewMember.getId(), squadId)
        );
    }
}
