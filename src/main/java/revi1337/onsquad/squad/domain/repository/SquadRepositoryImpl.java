package revi1337.onsquad.squad.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;

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
    public Optional<Squad> findById(Long id) {
        return squadJpaRepository.findById(id);
    }

    @Override
    public Optional<Squad> findWithCrewById(Long id) {
        return squadJpaRepository.findWithCrewById(id);
    }

    @Override
    public Optional<Squad> findSquadWithDetailById(Long id) {
        return squadQueryDslRepository.findSquadWithDetailById(id);
    }

    @Override
    public List<SquadResult> fetchSquadsWithDetailByCrewIdAndCategory(Long crewId, CategoryType categoryType, Pageable pageable) {
        return squadQueryDslRepository.fetchSquadsWithDetailByCrewIdAndCategory(crewId, categoryType, pageable);
    }

    @Override
    public List<SquadWithLeaderStateResult> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        return squadQueryDslRepository.fetchManageList(memberId, crewId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        squadJpaRepository.deleteById(id);
    }
}
