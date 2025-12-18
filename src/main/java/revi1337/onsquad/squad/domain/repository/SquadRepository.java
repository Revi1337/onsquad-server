package revi1337.onsquad.squad.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;
import revi1337.onsquad.squad.domain.entity.Squad;

public interface SquadRepository {

    Squad getReferenceById(Long id);

    Squad save(Squad squad);

    Squad saveAndFlush(Squad squad);

    boolean existsByIdAndCrewId(Long squadId, Long crewId);

    Optional<Squad> findById(Long id);

    Optional<Squad> findWithCrewById(Long id);

    Optional<SquadDomainDto> fetchById(Long id);

    Page<SquadDomainDto> fetchAllByCrewId(Long crewId, CategoryType categoryType, Pageable pageable);

    Page<SquadWithLeaderStateDomainDto> fetchAllWithOwnerState(Long memberId, Long crewId, Pageable pageable);

    void deleteById(Long id);

}
