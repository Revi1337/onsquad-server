package revi1337.onsquad.crew.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagJdbcRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.hashtag.domain.Hashtag;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class CrewRepositoryImpl implements CrewRepository {

    private final CrewJpaRepository crewJpaRepository;
    private final CrewQueryDslRepository crewQueryDslRepository;
    private final CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Transactional
    @Override
    public Crew persistCrew(Crew crew, List<Hashtag> hashtags) {
        crew.addCrewMember(CrewMember.forOwner(crew.getMember(), LocalDateTime.now()));
        Crew persistCrew = crewJpaRepository.save(crew);
        crewHashtagJdbcRepository.batchInsertCrewHashtags(persistCrew.getId(), hashtags);
        return persistCrew;
    }

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
    public Optional<Crew> findByIdWithCrewMembers(Long id) {
        return crewJpaRepository.findByIdWithCrewMembers(id);
    }
}
