package revi1337.onsquad.crew.domain.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;

public interface CrewJpaRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(Name name);

    @Modifying(clearAutomatically = true)
    @Query("delete Crew as c where c.id = :id")
    void deleteById(Long id);

    @Lock(PESSIMISTIC_WRITE)
    @Query("select c from Crew c where c.id = :id")
    Optional<Crew> findByIdForUpdate(Long id);

    List<Crew> findAllByMemberId(Long memberId);

}
