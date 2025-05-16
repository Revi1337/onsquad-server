package revi1337.onsquad.squad.domain;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadRepositoryImpl implements SquadRepository {

    private final SquadJpaRepository squadJpaRepository;
    private final SquadQueryDslRepository squadQueryDslRepository;

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
    public Optional<Squad> findByIdWithMembers(Long id) {
        return squadJpaRepository.findByIdWithMembers(id);
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
}
