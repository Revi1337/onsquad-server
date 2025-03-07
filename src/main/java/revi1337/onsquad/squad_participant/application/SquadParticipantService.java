package revi1337.onsquad.squad_participant.application;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.application.dto.SimpleSquadParticipantDto;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;
import revi1337.onsquad.squad_participant.domain.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadParticipantService {

    private final SquadParticipantRepository squadParticipantRepository;
    private final CrewRepository crewRepository;
    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public List<SquadParticipantRequestDto> findMySquadRequests(Long memberId) {
        return squadParticipantRepository.findSquadParticipantRequestsByMemberId(memberId).stream()
                .map(SquadParticipantRequestDto::from)
                .toList();
    }

    public void rejectSquadRequest(Long memberId, Long crewId, Long squadId) {
        SquadParticipant squadParticipant = squadParticipantRepository
                .getByCrewIdAndSquadIdAndMemberId(crewId, squadId, memberId);
        squadParticipantRepository.deleteById(squadParticipant.getId());
    }

    public void findRequestsInMySquad(Long memberId, Long crewId, Long squadId) {
        Crew crew = crewRepository.getById(crewId);
        Squad squad = squadRepository.getById(squadId);
    }

    public List<SimpleSquadParticipantDto> fetchRequestsInSquad(
            Long memberId, Long crewId, Long squadId, Pageable pageable
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
}
