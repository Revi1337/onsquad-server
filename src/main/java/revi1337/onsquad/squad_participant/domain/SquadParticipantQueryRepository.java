package revi1337.onsquad.squad_participant.domain;

import java.util.Optional;

public interface SquadParticipantQueryRepository {

    Optional<SquadParticipant> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId);

}
