package revi1337.onsquad.squad.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;

public interface SquadRepository {

    Squad save(Squad squad);

    Squad saveAndFlush(Squad squad);

    Optional<Squad> findSquadWithMembersById(Long id, Title title);

    Optional<Squad> findById(Long id);

    Optional<SquadInfoDomainDto> findSquadById(Long id);

    Page<SquadInfoDomainDto> findSquadsByCrewName(Name crewName, CategoryType categoryType, Pageable pageable);

    List<SimpleSquadInfoDomainDto> findSquadsInCrew(Long memberId, Name CrewName);

    Optional<Squad> findSquadByIdWithCrew(Long id);

    Optional<Squad> findSquadByIdWithCrewAndCrewMembers(Long id);

    Optional<Squad> findSquadByIdWithSquadMembers(Long id);

    Optional<Squad> findByIdWithCrewAndCrewMembers(Long id);

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

    default Squad getByIdWithCrewAndCrewMembers(Long id) {
        return findByIdWithCrewAndCrewMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default Squad getByIdWithOwnerAndCrewAndSquadMembers(Long id) {
        return findByIdWithOwnerAndCrewAndSquadMembers(id)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }
}
