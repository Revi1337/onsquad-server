package revi1337.onsquad.squad.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Optional;

public interface SquadQueryRepository {

    Optional<Squad> findSquadWithMembersById(Long id, Title title);

    Optional<Squad> findSquadWithMemberByIdAndTitle(Long id, Title title);

    Page<Squad> findSquadsByCrewName(Name crewName, Pageable pageable);

}
