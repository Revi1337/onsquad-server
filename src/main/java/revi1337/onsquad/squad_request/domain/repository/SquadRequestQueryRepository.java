package revi1337.onsquad.squad_request.domain.repository;

import java.util.Optional;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public interface SquadRequestQueryRepository {

    Optional<SquadRequest> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId);

}
