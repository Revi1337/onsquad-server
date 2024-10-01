package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

import java.time.LocalDateTime;

import static revi1337.onsquad.squad.error.SquadErrorCode.*;
import static revi1337.onsquad.squad_participant.error.SquadParticipantErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadConfigService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadParticipantRepository squadParticipantRepository;

    // TODO 일단 동시성 문제는 나중에 해결
    @Transactional
    public void acceptSquadMember(SquadAcceptDto dto) {
        Squad squad = squadRepository.getSquadByIdWithCrew(dto.squadId());
        checkCrewInfoMatch(dto, squad);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(squad.getCrew().getId(), dto.requestMemberId());
        squadParticipantRepository.findBySquadIdAndCrewMemberId(squad.getId(), crewMember.getId()).ifPresentOrElse(
                squadParticipant -> {
                    squadParticipantRepository.delete(squadParticipant);
                    squad.addSquadMember(SquadMember.forGeneral(crewMember, LocalDateTime.now()));
                    squadRepository.saveAndFlush(squad);
                },
                () -> { throw new SquadParticipantBusinessException.NeverRequested(NEVER_REQUESTED); }
        );
    }

    private void checkCrewInfoMatch(SquadAcceptDto dto, Squad squad) {
        if (!squad.getCrew().getName().equals(new Name(dto.requestCrewName()))) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }
}
