package revi1337.onsquad.squad.domain.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;
import revi1337.onsquad.squad.domain.entity.Squad;

@RequiredArgsConstructor
@Repository
public class SquadRepositoryImpl implements SquadRepository {

    private final SquadJpaRepository squadJpaRepository;
    private final SquadQueryDslRepository squadQueryDslRepository;

    @Override
    public Squad getReferenceById(Long id) {
        return squadJpaRepository.getReferenceById(id);
    }

    @Override
    public Squad save(Squad squad) {
        return squadJpaRepository.save(squad);
    }

    @Override
    public Squad saveAndFlush(Squad squad) {
        return squadJpaRepository.saveAndFlush(squad);
    }

    @Override
    public boolean existsByIdAndCrewId(Long id, Long crewId) {
        return squadJpaRepository.existsByIdAndCrew_id(id, crewId);
    }

    @Override
    public Optional<Squad> findById(Long id) {
        return squadJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadDomainDto> fetchById(Long id) {
        return squadQueryDslRepository.fetchById(id);
    }

    @Override
    public Page<SquadDomainDto> fetchAllByCrewId(Long crewId, CategoryType categoryType, Pageable pageable) {
        return squadQueryDslRepository.fetchAllByCrewId(crewId, categoryType, pageable);
    }

    @Override
    public Page<SquadWithLeaderStateDomainDto> fetchAllWithOwnerState(Long memberId, Long crewId, Pageable pageable) {
        return squadQueryDslRepository.fetchAllWithOwnerState(memberId, crewId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        squadJpaRepository.deleteById(id);
    }
}
