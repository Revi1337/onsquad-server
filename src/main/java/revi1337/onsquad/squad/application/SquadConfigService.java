package revi1337.onsquad.squad.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTMATCH_CREWINFO;
import static revi1337.onsquad.squad_participant.error.SquadParticipantErrorCode.NEVER_REQUESTED;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadConfigService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadParticipantRepository squadParticipantRepository;

    // TODO 일단 동시성 문제는 나중에 해결
    @Transactional
    public void acceptSquadMember(Long crewId, SquadAcceptDto dto) {
        Squad squad = squadRepository.getSquadByIdWithCrew(dto.squadId());
        checkCrewInfoMatch(crewId, squad);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(squad.getCrew().getId(), dto.memberId());
        squadParticipantRepository.findBySquadIdAndCrewMemberId(squad.getId(), crewMember.getId()).ifPresentOrElse(
                squadParticipant -> {
                    squadParticipantRepository.delete(squadParticipant);
                    squad.addSquadMember(SquadMember.forGeneral(crewMember, LocalDateTime.now()));
                    squadRepository.saveAndFlush(squad);
                },
                () -> {
                    throw new SquadParticipantBusinessException.NeverRequested(NEVER_REQUESTED);
                }
        );
    }

    private void checkCrewInfoMatch(Long requestCrewId, Squad squad) {
        if (!squad.getCrew().getId().equals(requestCrewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }
}
