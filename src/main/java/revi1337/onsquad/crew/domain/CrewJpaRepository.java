package revi1337.onsquad.crew.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew.domain.vo.Name;

public interface CrewJpaRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(Name name);

    @Modifying(clearAutomatically = true)
    @Query("delete Crew as c where c.id = :id")
    void deleteById(Long id);

}
