package revi1337.onsquad.squad_member.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public interface SquadMemberJpaRepository extends JpaRepository<SquadMember, Long> {

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    @Query("select sm from SquadMember sm inner join fetch sm.squad s where sm.squad.id = :squadId and sm.member.id = :memberId")
    Optional<SquadMember> findWithSquadBySquadIdAndMemberId(Long squadId, Long memberId);

    int countBySquadId(Long squadId);

}
