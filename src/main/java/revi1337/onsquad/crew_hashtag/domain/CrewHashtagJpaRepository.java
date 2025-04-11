package revi1337.onsquad.crew_hashtag.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CrewHashtagJpaRepository extends JpaRepository<CrewHashtag, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete CrewHashtag as ch where ch.crew.id = :crewId")
    void deleteByCrewId(Long crewId);

}
