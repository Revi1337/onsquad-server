package revi1337.onsquad.squad.domain;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

public interface SquadRepository {

    Squad save(Squad squad);

    Squad saveAndFlush(Squad squad);

    Optional<Squad> findById(Long id);

    Optional<Squad> findByIdWithMembers(Long id);

    Optional<SquadInfoDomainDto> fetchById(Long id);

    Page<SquadInfoDomainDto> fetchAllByCrewId(Long crewId, CategoryType categoryType, Pageable pageable);

    Page<SquadWithLeaderStateDomainDto> fetchAllWithOwnerState(Long memberId, Long crewId, Pageable pageable);

    default Squad getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default SquadInfoDomainDto getSquadById(Long id) {
        return fetchById(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getByIdWithMembers(Long id) {
        return findByIdWithMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }
}
