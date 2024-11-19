package revi1337.onsquad.crew_participant.application;

import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.CANT_SEE_PARTICIPANTS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_participant.application.dto.CrewParticipantRequestDto;
import revi1337.onsquad.crew_participant.application.dto.SimpleCrewParticipantRequestDto;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewParticipantService {

    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewRepository crewRepository;

    public List<CrewParticipantRequestDto> findMyCrewRequests(Long memberId) {
        return crewParticipantRepository.findMyCrewRequests(memberId).stream()
                .map(CrewParticipantRequestDto::from)
                .toList();
    }

    public List<SimpleCrewParticipantRequestDto> findCrewRequestsInMyCrew(Long memberId, Long crewId) {
        Crew crew = crewRepository.getById(crewId);
        validateMemberIsCrewCreator(memberId, crew);

        return crewParticipantRepository.findCrewRequestsInCrew(crewId).stream()
                .map(SimpleCrewParticipantRequestDto::from)
                .toList();
    }

    public void rejectCrewRequest(Long memberId, Long crewId) {
        CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);
        crewParticipantRepository.deleteById(crewParticipant.getId());
    }

    private void validateMemberIsCrewCreator(Long memberId, Crew crew) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewParticipantBusinessException.CantSeeParticipant(CANT_SEE_PARTICIPANTS);
        }
    }
}
