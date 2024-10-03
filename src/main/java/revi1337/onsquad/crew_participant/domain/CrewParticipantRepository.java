package revi1337.onsquad.crew_participant.domain;

import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.*;

public interface CrewParticipantRepository {

    CrewParticipant save(CrewParticipant crewParticipant);

    List<CrewParticipant> saveAll(List<CrewParticipant> crewParticipants);

    CrewParticipant saveAndFlush(CrewParticipant crewParticipant);

    void deleteById(Long id);

    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

    CrewParticipant upsertCrewParticipant(Long crewId, Long memberId, LocalDateTime now);

    List<CrewParticipantRequest> findMyCrewRequests(Long memberId);

    List<SimpleCrewParticipantRequest> findCrewRequestsInCrew(Long crewId);

    default CrewParticipant getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
