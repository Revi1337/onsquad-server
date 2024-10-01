package revi1337.onsquad.squad.domain;

import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findSquadWithMembersById(Long id, Title title);

    Optional<Squad> findSquadByIdWithCrew(Long id);

    Optional<Squad> findSquadByIdWithCrewAndCrewMembers(Long id);

    Optional<Squad> findSquadByIdWithSquadMembers(Long id);

    Optional<Squad> findByIdWithCrewAndCrewMembers(Long id);

    Optional<Squad> findByIdWithOwnerAndCrewAndSquadMembers(Long id);

}
