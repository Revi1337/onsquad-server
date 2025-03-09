package revi1337.onsquad.announce.domain;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

@RequiredArgsConstructor
@Repository
public class AnnounceRepositoryImpl implements AnnounceRepository {

    private final AnnounceJpaRepository announceJpaRepository;
    private final AnnounceCacheRepository announceCacheRepository;
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
    public List<AnnounceInfoDomainDto> fetchAnnouncesByCrewId(Long crewId) {
        return announceQueryDslRepository.fetchLimitedByCrewId(crewId, null);
    }

    @Override
    public List<AnnounceInfoDomainDto> findLimitedAnnouncesByCrewId(Long crewId) {
        return announceQueryDslRepository.fetchLimitedByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }

    @Override
    public List<AnnounceInfoDomainDto> fetchCachedLimitedAnnouncesByCrewId(Long crewId) {
        return announceCacheRepository.fetchCachedLimitedByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }

    @Override
    public AnnounceInfoDomainDto getCachedByCrewIdAndIdAndMemberId(Long crewId, Long announceId, Long memberId) {
        AnnounceInfoDomainDto announceInfo = announceCacheRepository
                .getCachedByCrewIdAndIdAndMemberId(crewId, announceId, memberId);
        if (announceInfo == null) {
            throw new AnnounceBusinessException.NotFoundById(NOT_FOUND, announceId);
        }
        return announceInfo;
    }
}
