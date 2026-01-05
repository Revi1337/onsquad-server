package revi1337.onsquad.announce.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.result.AnnounceReference;

public interface AnnounceJpaRepository extends JpaRepository<Announce, Long> {

    Optional<Announce> findByIdAndCrewId(Long id, Long crewId);

    @Query("select new revi1337.onsquad.announce.domain.result.AnnounceReference(a.crew.id, a.id) " +
            "from Announce a where a.member.id = :memberId")
    List<AnnounceReference> findAnnounceReferencesByMemberId(Long memberId);

}
