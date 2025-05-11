package revi1337.onsquad.squad_participant.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SquadParticipantJpaRepository extends JpaRepository<SquadParticipant, Long>,
        SquadParticipantQueryRepository {

    @Query("select sp from SquadParticipant as sp where sp.squad.id = :squadId and sp.crewMember.id = :crewMemberId")
    Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete SquadParticipant as sp where sp.id = :id")
    void deleteById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete SquadParticipant as sp where sp.squad.id = :squadId and sp.crewMember.id = :crewMemberId")
    int deleteBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

}
