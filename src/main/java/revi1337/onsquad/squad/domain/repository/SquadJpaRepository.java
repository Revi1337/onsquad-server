package revi1337.onsquad.squad.domain.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad.domain.entity.Squad;

public interface SquadJpaRepository extends JpaRepository<Squad, Long> {

    @EntityGraph(attributePaths = "crew")
    Optional<Squad> findWithCrewById(Long id);

    @Lock(PESSIMISTIC_WRITE)
    @Query("select s from Squad s where s.id = :id")
    Optional<Squad> findByIdForUpdate(Long id);

    @Query("select s.id from Squad s where s.member.id = :memberId")
    List<Long> findIdsByMemberId(Long memberId);

    @Query("select s.id from Squad s where s.crew.id in :crewIds")
    List<Long> findIdsByCrewIdIn(List<Long> crewIds);

    @Modifying
    @Query("delete Squad s where s.id in :ids")
    int deleteByIdIn(List<Long> ids);

    @Modifying
    @Query("delete Squad s where s.crew.id in :crewIds")
    int deleteByCrewIdIn(List<Long> crewIds);

    @Modifying
    @Query("update Squad s set s.currentSize = s.currentSize - 1, s.remain = s.remain + 1 " +
            "where s.id in (select sm.squad.id from SquadMember sm where sm.member.id = :memberId) " +
            "and s.currentSize > 0")
    int decrementCountByMemberId(Long memberId);

}
