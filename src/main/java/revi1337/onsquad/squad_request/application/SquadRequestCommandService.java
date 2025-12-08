package revi1337.onsquad.squad_request.application;

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
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;
import revi1337.onsquad.squad_request.error.SquadRequestErrorCode;
import revi1337.onsquad.squad_request.error.exception.SquadRequestBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadRequestCommandService {

    private final SquadRequestRepository squadRequestRepository;
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
        squadRequestRepository.findBySquadIdAndCrewMemberId(squadId, crewMember.getId()).ifPresentOrElse(
                squadParticipant -> {
                    squadParticipant.updateRequestAt(now);
                    squadRequestRepository.saveAndFlush(squadParticipant);
                },
                () -> squadRequestRepository.save(SquadRequest.of(squad, crewMember, now))
        );
    }

    public void acceptRequest(Long memberId, Long crewId, Long squadId, Long requestId) {
        checkMemberIsSquadLeader(memberId, crewId, squadId);
        SquadRequest participant = squadRequestRepository.getByIdWithSquad(requestId);
        if (participant.isNotMatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_REFERENCE);
        }

        Squad squad = participant.getSquad();
        CrewMember acceptMember = participant.getCrewMember();
        squad.addMembers(SquadMemberFactory.general(squad, acceptMember, LocalDateTime.now()));
        squadRepository.saveAndFlush(squad);
        squadRequestRepository.deleteById(requestId);
    }

    public void rejectRequest(Long memberId, Long crewId, Long squadId, Long requestId) {
        checkMemberIsSquadLeader(memberId, crewId, squadId);
        SquadRequest participant = squadRequestRepository.getById(requestId);
        if (participant.isNotMatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_REFERENCE);
        }
        squadRequestRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadRequest participant = squadRequestRepository
                .getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        squadRequestRepository.deleteById(participant.getId());
    }

    private void checkMemberIsSquadLeader(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(NOT_LEADER);
        }
    }
}
