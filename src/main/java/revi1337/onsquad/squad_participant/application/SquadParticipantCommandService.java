package revi1337.onsquad.squad_participant.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.squad.error.SquadErrorCode.MISMATCH_REFERENCE;
import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.domain.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;

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

        if (squad.isNotMatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(MISMATCH_REFERENCE);
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
        Squad squad = participant.getSquad();
        if (squad.isNotMatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(MISMATCH_REFERENCE);
        }

        CrewMember acceptMember = participant.getCrewMember();
        squad.addMembers(SquadMember.forGeneral(squad, acceptMember, LocalDateTime.now()));
        squadRepository.saveAndFlush(squad);
        squadParticipantRepository.deleteById(participant.getId());
    }

    public void rejectRequest(Long memberId, Long crewId, Long squadId, Long requestId) {
        checkMemberIsSquadLeader(memberId, crewId, squadId);
        Squad squad = squadRepository.getById(squadId);
        if (squad.isNotMatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(MISMATCH_REFERENCE);
        }

        squadParticipantRepository.findById(requestId)
                .filter(p -> p.matchSquadId(squadId))
                .ifPresent(p -> squadParticipantRepository.deleteById(requestId));
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
