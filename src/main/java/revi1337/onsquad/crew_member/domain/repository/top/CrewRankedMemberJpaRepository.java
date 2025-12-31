package revi1337.onsquad.crew_member.domain.repository.top;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;

public interface CrewRankedMemberJpaRepository extends JpaRepository<CrewRankedMember, Long> {

    @Query("SELECT ctm FROM CrewRankedMember ctm WHERE ctm.crewId = :crewId ORDER BY ctm.rank DESC")
    List<CrewRankedMember> findAllByCrewId(Long crewId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CrewRankedMember c")
    boolean exists();

}
