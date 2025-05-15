package revi1337.onsquad.crew_participant.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CrewParticipantJpaRepository extends JpaRepository<CrewParticipant, Long> {

    @Query("select cp from CrewParticipant cp where cp.crew.id = :crewId and cp.member.id = :memberId")
    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete CrewParticipant where id = :id")
    void deleteById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete CrewParticipant cp where cp.crew.id = :crewId and  cp.member.id = :memberId")
    void deleteByCrewIdAndMemberId(Long crewId, Long memberId);

}
