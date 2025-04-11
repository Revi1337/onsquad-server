package revi1337.onsquad.crew.domain;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;

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
    public void deleteById(Long id) {
        crewJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Crew> findById(Long id) {
        return crewJpaRepository.findById(id);
    }

    @Override
    public boolean existsByName(Name name) {
        return crewJpaRepository.existsByName(name);
    }

    @Override
    public Optional<CrewInfoDomainDto> findCrewById(Long id) {
        return crewQueryDslRepository.findCrewById(id);
    }

    @Override
    public Page<CrewInfoDomainDto> findCrewsByName(String name, Pageable pageable) {
        return crewQueryDslRepository.findCrewsByName(name, pageable);
    }
}
