package revi1337.onsquad.squad.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SquadJpaRepository extends JpaRepository<Squad, Long>, SquadQueryRepository {
}
