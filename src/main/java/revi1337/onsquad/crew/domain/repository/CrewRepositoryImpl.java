package revi1337.onsquad.crew.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.domain.result.EnrolledCrewResult;

@RequiredArgsConstructor
@Repository
public class CrewRepositoryImpl implements CrewRepository {

    private final CrewJpaRepository crewJpaRepository;
    private final CrewQueryDslRepository crewQueryDslRepository;

    @Override
    public Crew save(Crew crew) {
        return crewJpaRepository.save(crew);
    }

    @Override
    public Crew saveAndFlush(Crew crew) {
        return crewJpaRepository.saveAndFlush(crew);
    }

    @Override
    public Crew getReferenceById(Long id) {
        return crewJpaRepository.getReferenceById(id);
    }

    @Override
    public void deleteById(Long id) {
        crewJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Crew> findById(Long id) {
        return crewJpaRepository.findById(id);
    }

    @Override
    public Optional<Crew> findByIdForUpdate(Long id) {
        return crewJpaRepository.findByIdForUpdate(id);
    }

    @Override
    public boolean existsByName(Name name) {
        return crewJpaRepository.existsByName(name);
    }

    @Override
    public Optional<CrewResult> findCrewById(Long id) {
        return crewQueryDslRepository.findCrewById(id);
    }

    @Override
    public Page<CrewResult> fetchCrewsByName(String name, Pageable pageable) {
        return crewQueryDslRepository.fetchCrewsByName(name, pageable);
    }

    @Override
    public Page<CrewResult> fetchOwnedByMemberId(Long memberId, Pageable pageable) {
        return crewQueryDslRepository.fetchCrewsByMemberId(memberId, pageable);
    }

    @Override
    public List<EnrolledCrewResult> fetchParticipantsByMemberId(Long memberId) {
        return crewQueryDslRepository.fetchEnrolledCrewsByMemberId(memberId);
    }
}
