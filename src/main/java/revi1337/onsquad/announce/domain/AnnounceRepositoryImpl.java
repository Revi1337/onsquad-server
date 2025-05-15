package revi1337.onsquad.announce.domain;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;

@RequiredArgsConstructor
@Repository
public class AnnounceRepositoryImpl implements AnnounceRepository {

    private final AnnounceJpaRepository announceJpaRepository;
    private final AnnounceQueryDslRepository announceQueryDslRepository;
    private final AnnounceCacheRepository announceCacheRepository;

    @Override
    public Announce save(Announce announce) {
        return announceJpaRepository.save(announce);
    }

    @Override
    public Announce saveAndFlush(Announce announce) {
        return announceJpaRepository.saveAndFlush(announce);
    }

    @Override
    public void delete(Announce announce) {
        announceJpaRepository.delete(announce);
    }

    @Override
    public Optional<Announce> findByIdAndCrewId(Long id, Long crewId) {
        return announceJpaRepository.findByIdAndCrewId(id, crewId);
    }

    @Override
    public Page<AnnounceDomainDto> fetchAllByCrewId(Long crewId, Pageable pageable) {
        return announceQueryDslRepository.fetchAllByCrewId(crewId, pageable);
    }

    @Override
    public AnnounceDomainDto fetchCacheByCrewIdAndId(Long crewId, Long id) {
        return announceCacheRepository.fetchCacheByCrewIdAndId(crewId, id);
    }
}
