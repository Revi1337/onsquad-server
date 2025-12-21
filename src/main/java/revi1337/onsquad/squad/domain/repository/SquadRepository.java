package revi1337.onsquad.squad.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;

public interface SquadRepository {

    Squad getReferenceById(Long id);

    Squad save(Squad squad);

    Squad saveAndFlush(Squad squad);

    Optional<Squad> findById(Long id);

    Optional<Squad> findWithCrewById(Long id);

    Optional<Squad> findSquadWithDetailById(Long id);

    List<SquadResult> fetchSquadsWithDetailByCrewIdAndCategory(Long crewId, CategoryType categoryType, Pageable pageable);

    List<SquadWithLeaderStateResult> fetchManageList(Long memberId, Long crewId, Pageable pageable);

    void deleteById(Long id);

}
