package revi1337.onsquad.squad.domain;

import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findSquadWithMembersById(Long id, Title title);

    Optional<Squad> findSquadByIdAndTitleWithMember(Long id);

    Optional<Squad> findSquadWithCrewById(Long id);

    Optional<Squad> findSquadByIdWithCrewAndCrewMembers(Long id);

    Optional<Squad> findSquadByIdWithSquadMembers(Long id);

}
