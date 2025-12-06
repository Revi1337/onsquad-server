package revi1337.onsquad.squad_participant.domain.repository;

import java.util.Optional;
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;

public interface SquadParticipantQueryRepository {

    Optional<SquadParticipant> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId);

}
