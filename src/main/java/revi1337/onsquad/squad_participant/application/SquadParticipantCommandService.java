package revi1337.onsquad.squad_participant.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.squad.error.SquadErrorCode.NOTMATCH_CREWINFO;
import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;

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

    public void cancelMyRequest(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadParticipant participant = squadParticipantRepository
                .getBySquadIdAndCrewMemberId(squadId, crewMember.getId());

        squadParticipantRepository.deleteById(participant.getId());
    }

    public void request(Long memberId, Long crewId, Long squadId) {
        Squad squad = squadRepository.getByIdWithMembers(squadId);
        if (squad.doesNotMatchCrewId(crewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }

        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(squad.getCrewId(), memberId);
        if (squad.existsMember(crewMember.getId())) {
            throw new SquadBusinessException.AlreadyParticipant(ALREADY_JOIN, squad.getTitle().getValue());
        }

        squadParticipantRepository.upsertSquadParticipant(squad.getId(), crewMember.getId(), LocalDateTime.now());
    }

    public void acceptRequest(Long memberId, Long crewId, Long squadId, SquadAcceptDto dto) {
        validateAuthority(memberId, crewId, squadId);
        Squad squad = squadRepository.getById(squadId);
        if (squad.doesNotMatchCrewId(crewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }

        CrewMember acceptMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, dto.memberId());
        squad.addMembers(SquadMember.forGeneral(squad, acceptMember, LocalDateTime.now()));
        squadRepository.saveAndFlush(squad);
        squadParticipantRepository.deleteBySquadIdCrewMemberId(squad.getId(), acceptMember.getId());
    }

    public void rejectRequest(Long memberId, Long crewId, Long squadId, Long requestId) {
        validateAuthority(memberId, crewId, squadId);
        Squad squad = squadRepository.getById(squadId);
        if (squad.doesNotMatchCrewId(crewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }

        squadParticipantRepository.findById(requestId)
                .filter(p -> p.matchSquadId(squadId))
                .ifPresent(p -> squadParticipantRepository.deleteById(requestId));
    }

    private void validateAuthority(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(NOT_LEADER);
        }
    }
}
