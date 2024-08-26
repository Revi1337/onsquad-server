package revi1337.onsquad.squad.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.domain.category.Category;
import revi1337.onsquad.squad.domain.squad_category.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad.domain.vo.category.CategoryType;
import revi1337.onsquad.squad.dto.request.CategoryCondition;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SquadRepositoryImpl implements SquadRepository {

    private final SquadJpaRepository squadJpaRepository;
    private final SquadQueryDslRepository squadQueryDslRepository;
    private final SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    @Override
    public Squad save(Squad squad) {
        return squadJpaRepository.save(squad);
    }

    @Override
    public Optional<Squad> findSquadWithMembersById(Long id, Title title) {
        return squadJpaRepository.findSquadWithMembersById(id, title);
    }

    @Override
    public Optional<Squad> findSquadByIdAndTitleWithMember(Long id) {
        return squadJpaRepository.findSquadByIdAndTitleWithMember(id);
    }

    @Override
    public Page<Squad> findSquadsByCrewName(Name crewName, CategoryType categoryType, Pageable pageable) {
        return squadQueryDslRepository.findSquadsByCrewName(crewName, categoryType, pageable);
    }

    @Override
    public Optional<Squad> findSquadWithCrewById(Long id) {
        return squadJpaRepository.findSquadWithCrewById(id);
    }

    @Override
    public Optional<Squad> findSquadByIdWithCrewAndCrewMembers(Long id) {
        return squadJpaRepository.findSquadByIdWithCrewAndCrewMembers(id);
    }

    @Override
    public Optional<Squad> findSquadByIdWithSquadMembers(Long id) {
        return squadJpaRepository.findSquadByIdWithSquadMembers(id);
    }

    @Override
    public void batchInsertSquadCategories(Long squadId, List<Category> categories) {
        squadCategoryJdbcRepository.batchInsertSquadCategories(squadId, categories);
    }
}
