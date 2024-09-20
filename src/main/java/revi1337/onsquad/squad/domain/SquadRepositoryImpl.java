package revi1337.onsquad.squad.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad_category.domain.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.category.domain.vo.CategoryType;

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
    public Squad saveAndFlush(Squad squad) {
        return squadJpaRepository.saveAndFlush(squad);
    }

    @Override
    public Optional<Squad> findSquadWithMembersById(Long id, Title title) {
        return squadJpaRepository.findSquadWithMembersById(id, title);
    }

    @Override
    public Optional<Squad> findById(Long id) {
        return squadJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadInfoDomainDto> findSquadById(Long id) {
        return squadQueryDslRepository.findSquadById(id);
    }

    @Override
    public Page<SquadInfoDomainDto> findSquadsByCrewName(Name crewName, CategoryType categoryType, Pageable pageable) {
        return squadQueryDslRepository.findSquadsByCrewName(crewName, categoryType, pageable);
    }

    @Override
    public List<SimpleSquadInfoDomainDto> findSquadsInCrew(Long memberId, Name crewName) {
        return squadQueryDslRepository.findSquadsInCrew(memberId, crewName);
    }

    @Override
    public Optional<Squad> findSquadByIdWithCrew(Long id) {
        return squadJpaRepository.findSquadByIdWithCrew(id);
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
    public Optional<Squad> findByIdWithCrewAndCrewMembers(Long id) {
        return squadJpaRepository.findByIdWithCrewAndCrewMembers(id);
    }

    @Override
    public Optional<Squad> findByIdWithOwnerAndCrewAndSquadMembers(Long id) {
        return squadJpaRepository.findByIdWithOwnerAndCrewAndSquadMembers(id);
    }

    @Override
    public void batchInsertSquadCategories(Long squadId, List<Category> categories) {
        squadCategoryJdbcRepository.batchInsertSquadCategories(squadId, categories);
    }
}
