package revi1337.onsquad.squad_participant.domain;

import static revi1337.onsquad.squad_participant.error.SquadParticipantErrorCode.NEVER_REQUESTED;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

public interface SquadParticipantRepository {

    SquadParticipant save(SquadParticipant squadParticipant);

    Optional<SquadParticipant> findById(Long id);

    Optional<SquadParticipant> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId);

    Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId);

    Page<SimpleSquadParticipantDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable);

    void upsertSquadParticipant(Long squadId, Long crewMemberId, LocalDateTime now);

    void deleteById(Long id);

    void deleteBySquadIdCrewMemberId(Long squadId, Long crewMemberId);

    default SquadParticipant getByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId) {
        return findByCrewIdAndSquadIdAndMemberId(crewId, squadId, memberId)
                .orElseThrow(() -> new SquadParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }

    default SquadParticipant getBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return findBySquadIdAndCrewMemberId(squadId, crewMemberId)
                .orElseThrow(() -> new SquadParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
