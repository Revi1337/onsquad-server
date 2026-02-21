package revi1337.onsquad.announce.domain.repository;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.announce.domain.model.Announces;

public interface AnnounceRepository {

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    Optional<Announce> findById(Long id);

    Announces fetchAllByCrewId(Long crewId);

    List<AnnounceReference> findAnnounceReferencesByMemberId(Long memberId);

    void delete(Announce announce);

    int deleteByMemberId(Long memberId);

    int deleteByCrewIdIn(List<Long> crewIds);

    int markMemberAsNull(Long memberId);

}
