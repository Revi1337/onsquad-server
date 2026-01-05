package revi1337.onsquad.squad_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public interface SquadRequestJpaRepository extends JpaRepository<SquadRequest, Long> {

    @Query("select sr from SquadRequest sr where sr.squad.id = :squadId and sr.member.id = :memberId")
    Optional<SquadRequest> findBySquadIdAndMemberId(Long squadId, Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete SquadRequest sr where sr.id = :id")
    void deleteById(Long id);

    @Modifying
    @Query("delete SquadRequest sr where sr.member.id = :memberId")
    int deleteByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete SquadRequest sr where sr.squad.id = :squadId and sr.member.id = :memberId")
    int deleteBySquadIdAndMemberId(Long squadId, Long memberId);

    @Modifying
    @Query("delete from SquadRequest sr where sr.squad.id in :squadIds")
    int deleteBySquadIdIn(List<Long> squadIds);

}
