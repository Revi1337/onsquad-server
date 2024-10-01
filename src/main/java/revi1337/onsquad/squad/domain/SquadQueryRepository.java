package revi1337.onsquad.squad.domain;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findSquadByIdWithCrew(Long id);

    Optional<Squad> findSquadByIdWithSquadMembers(Long id);

    Optional<Squad> findByIdWithOwnerAndCrewAndSquadMembers(Long id);

}
