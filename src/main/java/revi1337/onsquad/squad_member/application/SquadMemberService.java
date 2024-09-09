package revi1337.onsquad.squad_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
import revi1337.onsquad.squad_member.application.dto.SquadWithMemberDto;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.domain.dto.SquadWithMemberDomainDto;

import java.util.List;

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

    public SquadWithMemberDto findSquadWithMembers(Long memberId, String crewName, Long squadId) {
        if (!crewMemberRepository.existsCrewMemberInCrew(memberId, new Name(crewName))) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }

        SquadWithMemberDomainDto squadWithMembers =
                squadMemberRepository.getSquadWithMembers(memberId, new Name(crewName), squadId);
        return SquadWithMemberDto.from(squadWithMembers);
    }
}
