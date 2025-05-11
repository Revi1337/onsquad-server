package revi1337.onsquad.squad.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SquadJpaRepository extends JpaRepository<Squad, Long> {

    @Query("select s from Squad s left join fetch s.members where s.id = :id")
    Optional<Squad> findByIdWithMembers(Long id);

}
