package revi1337.onsquad.participant.domain.crew_participant;

import revi1337.onsquad.participant.domain.crew_participant.dto.CrewParticipantRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CrewParticipantRepository {

    CrewParticipant save(CrewParticipant crewParticipant);

    List<CrewParticipant> saveAll(List<CrewParticipant> crewParticipants);

    CrewParticipant saveAndFlush(CrewParticipant crewParticipant);

    Optional<CrewParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    void upsertCrewParticipant(Long crewId, Long memberId, LocalDateTime now);

    List<CrewParticipantRequest> findCrewParticipantsByCrewNameAndMemberId(Long memberId);

}
