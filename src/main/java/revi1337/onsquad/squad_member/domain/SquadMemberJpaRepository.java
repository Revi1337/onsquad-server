package revi1337.onsquad.squad_member.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SquadMemberJpaRepository extends JpaRepository<SquadMember, Long> {

    boolean existsBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    Optional<SquadMember> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

}
