package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public interface SquadMemberJpaRepository extends JpaRepository<SquadMember, Long> {

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    @Modifying
    @Query("delete SquadMember sm where sm.member.id = :memberId")
    int deleteByMemberId(Long memberId);

    @Modifying
    @Query("delete SquadMember sm where sm.squad.id in :squadIds")
    int deleteBySquadIdIn(List<Long> squadIds);

}
