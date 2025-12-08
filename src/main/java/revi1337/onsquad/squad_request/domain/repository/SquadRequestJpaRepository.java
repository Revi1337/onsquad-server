package revi1337.onsquad.squad_request.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public interface SquadRequestJpaRepository extends JpaRepository<SquadRequest, Long>, SquadRequestQueryRepository {

    @Query("select sp from SquadRequest as sp inner join fetch sp.squad where sp.id = :id")
    Optional<SquadRequest> findByIdWithSquad(Long id);

    @Query("select sp from SquadRequest as sp where sp.squad.id = :squadId and sp.crewMember.id = :crewMemberId")
    Optional<SquadRequest> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete SquadRequest as sp where sp.id = :id")
    void deleteById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete SquadRequest as sp where sp.squad.id = :squadId and sp.crewMember.id = :crewMemberId")
    int deleteBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

}
