package revi1337.onsquad.squad.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.domain.category.Category;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;

public interface SquadRepository {

    Squad save(Squad squad);

    Optional<Squad> findSquadWithMembersById(Long id, Title title);

    Optional<Squad> findSquadByIdAndTitleWithMember(Long id);

    Page<Squad> findSquadsByCrewName(Name crewName, Pageable pageable);

    Optional<Squad> findSquadWithCrewById(Long id);

    Optional<Squad> findSquadByIdWithCrewAndCrewMembers(Long id);

    Optional<Squad> findSquadByIdWithSquadMembers(Long id);

    void batchInsertSquadCategories(Long squadId, List<Category> categories);

    default Squad getSquadByIdAndTitleWithMember(Long id) {
        return findSquadByIdAndTitleWithMember(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getSquadByIdWithSquadMembers(Long id) {
        return findSquadByIdWithSquadMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }
}
