package revi1337.onsquad.squad.domain.repository;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

public interface SquadRepository {

    Squad save(Squad squad);

    Squad saveAndFlush(Squad squad);

    Optional<Squad> findById(Long id);

    Optional<Squad> findByIdWithMembers(Long id);

    Optional<SquadDomainDto> fetchById(Long id);

    Page<SquadDomainDto> fetchAllByCrewId(Long crewId, CategoryType categoryType, Pageable pageable);

    Page<SquadWithLeaderStateDomainDto> fetchAllWithOwnerState(Long memberId, Long crewId, Pageable pageable);

    void deleteById(Long id);

    default Squad getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default SquadDomainDto getSquadById(Long id) {
        return fetchById(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getByIdWithMembers(Long id) {
        return findByIdWithMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }
}
