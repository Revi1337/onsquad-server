package revi1337.onsquad.squad_participant.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.squad.error.SquadErrorCode.NOTMATCH_CREWINFO;
import static revi1337.onsquad.squad.error.SquadErrorCode.OWNER_CANT_PARTICIPANT;
import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;
import static revi1337.onsquad.squad_participant.error.SquadParticipantErrorCode.NEVER_REQUESTED;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.application.dto.SimpleSquadParticipantDto;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;
import revi1337.onsquad.squad_participant.domain.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadParticipantService {

    private final SquadParticipantRepository squadParticipantRepository;
    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public List<SquadParticipantRequestDto> fetchAllSquadRequests(Long memberId) {
        return squadParticipantRepository.findSquadParticipantRequestsByMemberId(memberId).stream()
                .map(SquadParticipantRequestDto::from)
                .toList();
    }

    @Transactional
    public void rejectSquadRequest(Long memberId, Long crewId, Long squadId) {
        SquadParticipant squadParticipant = squadParticipantRepository
                .getByCrewIdAndSquadIdAndMemberId(crewId, squadId, memberId);
        squadParticipantRepository.deleteById(squadParticipant.getId());
    }

    @Transactional
    public void requestInSquad(Long memberId, Long crewId, Long squadId) {
        Squad squad = squadRepository.getByIdWithOwnerAndCrewAndSquadMembers(squadId);
        checkSquadBelongsToCrew(crewId, squad);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(squad.getCrewId(), memberId);
        validateSquadMembers(squad, crewMember);
        squadParticipantRepository.upsertSquadParticipant(squad.getId(), crewMember.getId(), LocalDateTime.now());
    }

    // TODO 일단 동시성 문제는 나중에 해결
    @Transactional
    public void acceptCrewRequest(Long crewId, Long squadId, SquadAcceptDto dto) {
        Squad squad = squadRepository.getSquadByIdWithCrew(squadId);
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

    public List<SimpleSquadParticipantDto> fetchRequestsInSquad(
            Long memberId,
            Long crewId,
            Long squadId,
            Pageable pageable
    ) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(NOT_LEADER);
        }

        return squadParticipantRepository.fetchAllWithMemberBySquadId(squadId, pageable).stream()
                .map(SimpleSquadParticipantDto::from)
                .toList();
    }

    private void checkSquadBelongsToCrew(Long requestCrewId, Squad squad) {
        if (squad.isNotSameCrewId(requestCrewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }

    private void validateSquadMembers(Squad squad, CrewMember crewMember) {
        checkDifferenceSquadCreator(squad, crewMember);
        checkCrewMemberAlreadyInSquad(squad, crewMember);
    }

    private void checkDifferenceSquadCreator(Squad squad, CrewMember crewMember) {
        if (crewMember.isOwnerOfSquad(squad.getOwnerId())) {
            throw new SquadBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }

    private void checkCrewMemberAlreadyInSquad(Squad squad, CrewMember crewMember) {
        if (squad.isSquadMemberAlreadyParticipant(crewMember.getId())) {
            throw new SquadBusinessException.AlreadyParticipant(ALREADY_JOIN, squad.getTitle().getValue());
        }
    }

    private void checkCrewInfoMatch(Long requestCrewId, Squad squad) {
        if (squad.isNotSameCrewId(requestCrewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }
}
