package revi1337.onsquad.crew_participant.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_participant.domain.entity.CrewParticipant;

public interface CrewParticipantJpaRepository extends JpaRepository<CrewParticipant, Long> {

    @Query("select cp from CrewParticipant cp where cp.crew.id = :crewId and cp.member.id = :memberId")
    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @EntityGraph(attributePaths = "crew")
    Optional<CrewParticipant> findWithCrewById(Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete CrewParticipant where id = :id")
    void deleteById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete CrewParticipant cp where cp.crew.id = :crewId and  cp.member.id = :memberId")
    void deleteByCrewIdAndMemberId(Long crewId, Long memberId);

}
