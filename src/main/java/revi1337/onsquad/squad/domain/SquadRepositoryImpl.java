package revi1337.onsquad.squad.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoWithOwnerFlagDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad_category.domain.SquadCategoryJdbcRepository;

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
    public Optional<Squad> findById(Long id) {
        return squadJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadInfoDomainDto> findSquadById(Long id) {
        return squadQueryDslRepository.findSquadById(id);
    }

    @Override
    public Page<SquadInfoDomainDto> findSquadsByCrewId(Long crewId, CategoryType categoryType, Pageable pageable) {
        return squadQueryDslRepository.findSquadsByCrewId(crewId, categoryType, pageable);
    }

    @Override
    public Page<SimpleSquadInfoWithOwnerFlagDomainDto> fetchSquadsWithOwnerFlag(
            Long memberId, Long crewId, Pageable pageable
    ) {
        return squadQueryDslRepository.fetchSquadsWithOwnerFlagByMemberIdAndCrewId(memberId, crewId, pageable);
    }

    @Override
    public Optional<Squad> findSquadByIdWithCrew(Long id) {
        return squadJpaRepository.findSquadByIdWithCrew(id);
    }

    @Override
    public Optional<Squad> findSquadByIdWithSquadMembers(Long id) {
        return squadJpaRepository.findSquadByIdWithSquadMembers(id);
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
