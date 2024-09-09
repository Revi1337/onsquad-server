package revi1337.onsquad.participant.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SquadParticipantRepository {

    SquadParticipant save(SquadParticipant squadParticipant);

    List<SquadParticipant> saveAll(List<SquadParticipant> squadParticipants);

    SquadParticipant saveAndFlush(SquadParticipant squadParticipant);

    Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    void upsertSquadParticipant(Long squadId, Long crewMemberId, LocalDateTime now);

}
