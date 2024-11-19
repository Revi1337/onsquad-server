package revi1337.onsquad.squad.domain;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

public interface SquadRepository {

    Squad save(Squad squad);

    Squad saveAndFlush(Squad squad);

    Optional<Squad> findById(Long id);

    Optional<SquadInfoDomainDto> findSquadById(Long id);

    Page<SquadInfoDomainDto> findSquadsByCrewId(Long crewId, CategoryType categoryType, Pageable pageable);

    List<SimpleSquadInfoDomainDto> findSquadsInCrew(Long memberId, Long crewId);

    Optional<Squad> findSquadByIdWithCrew(Long id);

    Optional<Squad> findSquadByIdWithSquadMembers(Long id);

    Optional<Squad> findByIdWithOwnerAndCrewAndSquadMembers(Long id);

    void batchInsertSquadCategories(Long squadId, List<Category> categories);

    default Squad getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default SquadInfoDomainDto getSquadById(Long id) {
        return findSquadById(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getSquadByIdWithSquadMembers(Long id) {
        return findSquadByIdWithSquadMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getSquadByIdWithCrew(Long id) {
        return findSquadByIdWithCrew(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getByIdWithOwnerAndCrewAndSquadMembers(Long id) {
        return findByIdWithOwnerAndCrewAndSquadMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }
}
