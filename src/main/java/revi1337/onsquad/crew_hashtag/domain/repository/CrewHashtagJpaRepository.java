package revi1337.onsquad.crew_hashtag.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew_hashtag.domain.entity.CrewHashtag;

public interface CrewHashtagJpaRepository extends JpaRepository<CrewHashtag, Long> {

    @Query("select ch from CrewHashtag ch inner join fetch ch.hashtag where ch.crew.id in :crewIds")
    List<CrewHashtag> fetchHashtagsByCrewIdIn(List<Long> crewIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete CrewHashtag ch where ch.crew.id in :crewIds")
    int deleteByCrewIdIn(List<Long> crewIds);

}
