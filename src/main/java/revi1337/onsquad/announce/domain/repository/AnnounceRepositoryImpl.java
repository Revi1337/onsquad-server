package revi1337.onsquad.announce.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.announce.domain.result.AnnounceResult;

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
    public void delete(Announce announce) {
        announceJpaRepository.delete(announce);
    }

    @Override
    public Optional<Announce> findById(Long id) {
        return announceJpaRepository.findById(id);
    }

    @Override
    public List<AnnounceReference> findAnnounceReferencesByMemberId(Long memberId) {
        return announceJpaRepository.findAnnounceReferencesByMemberId(memberId);
    }

    @Override
    public List<AnnounceResult> fetchAllByCrewId(Long crewId) {
        return announceQueryDslRepository.fetchAllByCrewId(crewId);
    }
}
