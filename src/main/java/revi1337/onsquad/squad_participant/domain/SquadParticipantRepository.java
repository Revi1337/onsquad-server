package revi1337.onsquad.squad_participant.domain;

import static revi1337.onsquad.squad_participant.error.SquadParticipantErrorCode.NEVER_REQUESTED;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

public interface SquadParticipantRepository {

    SquadParticipant save(SquadParticipant squadParticipant);

    List<SquadParticipant> saveAll(List<SquadParticipant> squadParticipants);

    SquadParticipant saveAndFlush(SquadParticipant squadParticipant);

    void delete(SquadParticipant squadParticipant);

    void deleteById(Long id);

    Optional<SquadParticipant> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId);

    Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    void upsertSquadParticipant(Long squadId, Long crewMemberId, LocalDateTime now);

    List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId);

    default SquadParticipant getByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId) {
        return findByCrewIdAndSquadIdAndMemberId(crewId, squadId, memberId)
                .orElseThrow(() -> new SquadParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }

    default SquadParticipant getBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return findBySquadIdAndCrewMemberId(squadId, crewMemberId)
                .orElseThrow(() -> new SquadParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
