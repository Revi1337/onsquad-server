package revi1337.onsquad.squad_participant.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
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
}
