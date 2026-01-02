package revi1337.onsquad.squad.domain.repository;

import jakarta.persistence.LockModeType;
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

    @Modifying(clearAutomatically = true)
    @Query("delete Squad s where s.id = :id")
    void deleteById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Squad s where s.id = :id")
    Optional<Squad> findByIdForUpdate(Long id);

}
