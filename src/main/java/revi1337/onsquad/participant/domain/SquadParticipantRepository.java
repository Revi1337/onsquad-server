package revi1337.onsquad.participant.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SquadParticipantRepository extends JpaRepository<SquadParticipant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sp from SquadParticipant as sp where sp.squad.id = :squadId and sp.crewMember.id = :memberId")
    Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long memberId);

}
