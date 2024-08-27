package revi1337.onsquad.participant.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CrewParticipantRepository extends JpaRepository<CrewParticipant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cp from CrewParticipant cp where cp.crew.id = :crewId and cp.member.id = :memberId")
    Optional<CrewParticipant> findByCrewIdAndMemberIdUsingLock(Long crewId, Long memberId);

    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

}
