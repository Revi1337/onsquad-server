package revi1337.onsquad.squad_member.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SquadMemberJpaRepository extends JpaRepository<SquadMember, Long> {

    Optional<SquadMember> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    @Query("select sm from SquadMember sm inner join fetch sm.squad s where sm.squad.id = :squadId and sm.crewMember.id = :crewMemberId")
    Optional<SquadMember> findWithSquadBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    int countBySquadId(Long squadId);

}
