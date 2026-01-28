package revi1337.onsquad.crew.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.domain.result.CrewWithOwnerStateResult;

public interface CrewRepository {

    Crew save(Crew crew);

    Crew saveAndFlush(Crew crew);

    Crew getReferenceById(Long id);

    boolean existsByName(Name name);

    Optional<Crew> findById(Long id);

    Optional<Crew> findByIdForUpdate(Long id);

    Optional<CrewResult> fetchCrewWithDetailById(Long id);

    List<Crew> findAllByMemberId(Long memberId);

    Page<CrewResult> fetchCrewsWithDetailByName(String name, Pageable pageable);

    List<CrewWithOwnerStateResult> fetchCrewsWithStateByIdIn(List<Long> ids, Long currentMemberId);

    int deleteByIdIn(List<Long> ids);

    int decrementCountById(Long id);

    int decrementCountByMemberId(Long memberId);

}
