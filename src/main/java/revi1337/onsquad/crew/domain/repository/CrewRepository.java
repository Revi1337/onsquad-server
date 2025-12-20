package revi1337.onsquad.crew.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.result.CrewResult;

public interface CrewRepository {

    Crew save(Crew crew);

    Crew saveAndFlush(Crew crew);

    Crew getReferenceById(Long id);

    void deleteById(Long id);

    Optional<Crew> findById(Long id);

    Optional<Crew> findByIdForUpdate(Long id);

    boolean existsByName(Name name);

    Optional<CrewResult> fetchCrewWithDetailById(Long id);

    List<CrewResult> fetchCrewsWithDetailByName(String name, Pageable pageable);

    List<CrewResult> fetchCrewsWithDetailByMemberId(Long memberId, Pageable pageable);

}
