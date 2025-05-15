package revi1337.onsquad.crew_participant.domain;

import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.NEVER_REQUESTED;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;

public interface CrewParticipantJpaRepository extends JpaRepository<CrewParticipant, Long> {

    @Query("select cp from CrewParticipant cp where cp.crew.id = :crewId and cp.member.id = :memberId")
    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete CrewParticipant where id = :crewParticipantId")
    void deleteById(Long crewParticipantId);

    @Modifying
    @Query("delete CrewParticipant cp where cp.crew.id = :crewId and  cp.member.id = :memberId")
    void deleteByCrewIdAndMemberId(Long crewId, Long memberId);

    default CrewParticipant getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
