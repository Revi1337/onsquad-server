package revi1337.onsquad.squad.domain;

import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findSquadWithMembersById(Long id, Title title);

    Optional<Squad> findSquadWithMemberByIdAndTitle(Long id, Title title);

}
