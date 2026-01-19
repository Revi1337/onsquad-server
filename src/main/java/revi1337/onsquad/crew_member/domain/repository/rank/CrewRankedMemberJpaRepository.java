package revi1337.onsquad.crew_member.domain.repository.rank;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;

public interface CrewRankedMemberJpaRepository extends JpaRepository<CrewRankedMember, Long> {

    boolean existsBy();

    List<CrewRankedMember> findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(Long crewId, int rank);

}
