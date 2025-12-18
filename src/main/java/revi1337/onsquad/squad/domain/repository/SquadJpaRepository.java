package revi1337.onsquad.squad.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad.domain.entity.Squad;

public interface SquadJpaRepository extends JpaRepository<Squad, Long> {

    boolean existsByIdAndCrew_id(Long id, Long crewId);

    @Query("select s from Squad s left join fetch s.members where s.id = :id")
    Optional<Squad> findByIdWithMembers(Long id);

    @EntityGraph(attributePaths = "crew")
    Optional<Squad> findWithCrewById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete Squad s where s.id = :id")
    void deleteById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete Squad s where s.crew.id = :crewId")
    void deleteByCrewId(Long crewId);

}
