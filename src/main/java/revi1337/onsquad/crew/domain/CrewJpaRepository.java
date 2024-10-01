package revi1337.onsquad.crew.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.Optional;

public interface CrewJpaRepository extends JpaRepository<Crew, Long>, CrewQueryRepository {

    Optional<Crew> findByName(Name name);

    boolean existsByName(Name name);

    @Query("select c from Crew as c inner join c.hashtags as ch where c.name = :name")
    Optional<Crew> findByNameWithHashtags(Name name);
}
