package revi1337.onsquad.squad.domain;

import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findSquadWithMembersById(Long squadId, Title squadTitle);

}
