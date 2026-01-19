package revi1337.onsquad.crew_member.domain.repository.rank;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;

public interface CrewRankedMemberJpaRepository extends JpaRepository<CrewRankedMember, Long> {

    @Query("select crm from CrewRankedMember crm where crm.crewId = :crewId order by crm.rank")
    List<CrewRankedMember> findAllByCrewId(Long crewId);

    @Query("select case when COUNT(crm.id) > 0 then true else false end from CrewRankedMember crm")
    boolean exists();

}
