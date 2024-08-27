package revi1337.onsquad.crew.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;

import java.util.List;
import java.util.Optional;

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
    public Optional<Crew> findByName(Name name) {
        return crewJpaRepository.findByName(name);
    }

    @Override
    public boolean existsByName(Name name) {
        return crewJpaRepository.existsByName(name);
    }

    @Override
    public List<Crew> findAllByMemberId(Long memberId) {
        return crewJpaRepository.findAllByMemberId(memberId);
    }

    @Override
    public Optional<CrewWithMemberAndImageDto> findCrewByName(Name name) {
        return crewQueryDslRepository.findCrewByName(name);
    }

    @Override
    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return crewQueryDslRepository.findCrewsByName();
    }

    @Override
    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return crewJpaRepository.findOwnedCrews(memberId);
    }

    @Override
    public Optional<Crew> findCrewByNameWithImage(Name name) {
        return crewJpaRepository.findCrewByNameWithImage(name);
    }

    @Override
    public Optional<Crew> findByNameWithCrewMembers(Name name) {
        return crewJpaRepository.findCrewByNameWithCrewMembers(name);
    }
}
