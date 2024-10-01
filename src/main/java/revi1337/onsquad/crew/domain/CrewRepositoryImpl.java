package revi1337.onsquad.crew.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagJdbcRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CrewRepositoryImpl implements CrewRepository {

    private final CrewJpaRepository crewJpaRepository;
    private final CrewQueryDslRepository crewQueryDslRepository;
    private final CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Override
    public Crew save(Crew crew) {
        return crewJpaRepository.save(crew);
    }

    @Override
    public Crew saveAndFlush(Crew crew) {
        return crewJpaRepository.saveAndFlush(crew);
    }

    @Override
    public Optional<Crew> findById(Long id) {
        return crewJpaRepository.findById(id);
    }

    @Override
    public Optional<Crew> findByName(Name name) {
        return crewJpaRepository.findByName(name);
    }

    @Override
    public Optional<Crew> findByNameWithHashtags(Name name) {
        return crewJpaRepository.findByNameWithHashtags(name);
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

    @Override
    public Optional<Crew> findByIdWithImage(Long id) {
        return crewJpaRepository.findByIdWithImage(id);
    }

    @Override
    public Optional<Crew> findByNameWithCrewMembers(Name name) {
        return crewJpaRepository.findByNameWithCrewMembers(name);
    }

    @Override
    public void batchInsertCrewHashtags(Long crewId, List<Hashtag> hashtags) {
        crewHashtagJdbcRepository.batchInsertCrewHashtags(crewId, hashtags);
    }
}
