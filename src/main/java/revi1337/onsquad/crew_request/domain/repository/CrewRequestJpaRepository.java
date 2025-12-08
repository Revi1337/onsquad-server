package revi1337.onsquad.crew_request.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

public interface CrewRequestJpaRepository extends JpaRepository<CrewRequest, Long> {

    @Query("select cp from CrewRequest cp where cp.crew.id = :crewId and cp.member.id = :memberId")
    Optional<CrewRequest> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @EntityGraph(attributePaths = "crew")
    Optional<CrewRequest> findWithCrewById(Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete CrewRequest where id = :id")
    void deleteById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete CrewRequest cp where cp.crew.id = :crewId and  cp.member.id = :memberId")
    void deleteByCrewIdAndMemberId(Long crewId, Long memberId);

}
