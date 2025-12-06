package revi1337.onsquad.squad_participant.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.repository.SquadParticipantRepository;
import revi1337.onsquad.squad_participant.error.SquadParticipantErrorCode;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadParticipantCommandService {

    private final SquadParticipantRepository squadParticipantRepository;
    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public void request(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Squad squad = squadRepository.getByIdWithMembers(squadId);
        if (squad.misMatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_REFERENCE);
        }
        if (squad.existsMember(crewMember.getId())) {
            throw new SquadBusinessException.AlreadyParticipant(ALREADY_JOIN, squad.getTitle().getValue());
        }

        LocalDateTime now = LocalDateTime.now();
        squadParticipantRepository.findBySquadIdAndCrewMemberId(squadId, crewMember.getId()).ifPresentOrElse(
                squadParticipant -> {
                    squadParticipant.updateRequestAt(now);
                    squadParticipantRepository.saveAndFlush(squadParticipant);
                },
                () -> squadParticipantRepository.save(SquadParticipant.of(squad, crewMember, now))
        );
    }

    public void acceptRequest(Long memberId, Long crewId, Long squadId, Long requestId) {
        checkMemberIsSquadLeader(memberId, crewId, squadId);
        SquadParticipant participant = squadParticipantRepository.getByIdWithSquad(requestId);
        if (participant.isNotMatchSquadId(squadId)) {
            throw new SquadParticipantBusinessException.MismatchReference(SquadParticipantErrorCode.MISMATCH_REFERENCE);
        }

        Squad squad = participant.getSquad();
        CrewMember acceptMember = participant.getCrewMember();
        squad.addMembers(SquadMember.forGeneral(squad, acceptMember, LocalDateTime.now()));
        squadRepository.saveAndFlush(squad);
        squadParticipantRepository.deleteById(requestId);
    }

    public void rejectRequest(Long memberId, Long crewId, Long squadId, Long requestId) {
        checkMemberIsSquadLeader(memberId, crewId, squadId);
        SquadParticipant participant = squadParticipantRepository.getById(requestId);
        if (participant.isNotMatchSquadId(squadId)) {
            throw new SquadParticipantBusinessException.MismatchReference(SquadParticipantErrorCode.MISMATCH_REFERENCE);
        }

        squadParticipantRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadParticipant participant = squadParticipantRepository
                .getBySquadIdAndCrewMemberId(squadId, crewMember.getId());

        squadParticipantRepository.deleteById(participant.getId());
    }

    private void checkMemberIsSquadLeader(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(NOT_LEADER);
        }
    }
}
