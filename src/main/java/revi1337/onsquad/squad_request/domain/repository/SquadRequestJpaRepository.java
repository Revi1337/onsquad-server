package revi1337.onsquad.squad_request.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public interface SquadRequestJpaRepository extends JpaRepository<SquadRequest, Long> {
    
    @Query("select sp from SquadRequest as sp where sp.squad.id = :squadId and sp.member.id = :memberId")
    Optional<SquadRequest> findBySquadIdAndMemberId(Long squadId, Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete SquadRequest as sp where sp.id = :id")
    void deleteById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete SquadRequest as sp where sp.squad.id = :squadId and sp.member.id = :memberId")
    int deleteBySquadIdAndMemberId(Long squadId, Long memberId);

}
