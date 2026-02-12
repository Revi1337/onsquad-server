package revi1337.onsquad.crew_member.domain.repository.rank;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;

public interface CrewRankerJpaRepository extends JpaRepository<CrewRanker, Long> {

    boolean existsBy();

    List<CrewRanker> findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(Long crewId, int rank);

}
