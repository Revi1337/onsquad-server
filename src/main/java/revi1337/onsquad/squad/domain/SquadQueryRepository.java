package revi1337.onsquad.squad.domain;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findByIdWithOwnerAndCrewAndSquadMembers(Long id);

}
