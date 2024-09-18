package revi1337.onsquad.announce.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class AnnounceRepositoryImpl implements AnnounceRepository {

    private final AnnounceJpaRepository announceJpaRepository;
    private final AnnounceQueryDslRepository announceQueryDslRepository;

    @Override
    public Announce save(Announce announce) {
        return announceJpaRepository.save(announce);
    }

    @Override
    public Announce saveAndFlush(Announce announce) {
        return announceJpaRepository.saveAndFlush(announce);
    }

    @Override
    public Optional<Announce> findByIdAndCrewId(Long crewId, Long id) {
        return announceJpaRepository.findByIdAndCrewId(id, crewId);
    }

    @Override
    public Optional<AnnounceInfoDomainDto> findAnnounceByCrewIdAndId(Long crewId, Long id, Long memberId) {
        return announceQueryDslRepository.findAnnounceByCrewIdAndId(crewId, id, memberId);
    }

    @Override
    public List<AnnounceInfoDomainDto> findAnnouncesByCrewId(Long crewId) {
        return announceQueryDslRepository.findAnnouncesByCrewId(crewId, null);
    }

    @Override
    public List<AnnounceInfoDomainDto> findLimitedAnnouncesByCrewId(Long crewId) {
        return announceQueryDslRepository.findAnnouncesByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }
}
