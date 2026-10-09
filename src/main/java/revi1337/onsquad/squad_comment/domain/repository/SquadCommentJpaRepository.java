package revi1337.onsquad.squad_comment.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

public interface SquadCommentJpaRepository extends JpaRepository<SquadComment, Long> {

    @EntityGraph(attributePaths = "squad")
    Optional<SquadComment> findWithSquadById(Long id);

    @Modifying
    @Query("delete SquadComment sc where sc.member.id = :memberId")
    int deleteByMemberId(Long memberId);

    @Query("select r.id from SquadComment r where r.parent.member.id = :memberId")
    List<Long> findReplyIdsByParentWriterId(Long memberId);

    @Modifying
    @Query("delete SquadComment sc where sc.id in :ids")
    int deleteByIdIn(List<Long> ids);

    @Modifying
    @Query("delete SquadComment sc where sc.squad.id in :squadIds and sc.parent is not null")
    int deleteRepliesBySquadIdIn(List<Long> squadIds);

    @Modifying
    @Query("delete SquadComment sc where sc.squad.id in :squadIds and sc.parent is null")
    int deleteParentsBySquadIdIn(List<Long> squadIds);

}
