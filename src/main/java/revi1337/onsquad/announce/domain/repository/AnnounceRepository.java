package revi1337.onsquad.announce.domain.repository;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.announce.domain.result.AnnounceResult;

public interface AnnounceRepository {

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    Optional<Announce> findById(Long id);

    List<AnnounceReference> findAnnounceReferencesByMemberId(Long memberId);

    List<AnnounceResult> fetchAllByCrewId(Long crewId);

    void delete(Announce announce);

    int deleteByMemberId(Long memberId);

    int deleteByCrewIdIn(List<Long> crewIds);

    int markMemberAsNull(Long memberId);

}
