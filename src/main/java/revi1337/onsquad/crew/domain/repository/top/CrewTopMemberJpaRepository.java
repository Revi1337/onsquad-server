package revi1337.onsquad.crew.domain.repository.top;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;

public interface CrewTopMemberJpaRepository extends JpaRepository<CrewTopMember, Long> {

    List<CrewTopMember> findAllByCrewId(Long crewId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CrewTopMember c")
    boolean exists();

}
