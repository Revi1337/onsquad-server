package revi1337.onsquad.squad_request.domain.repository;

import static revi1337.onsquad.squad_request.error.SquadRequestErrorCode.NEVER_REQUESTED;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadAndCrewDomainDto;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.error.exception.SquadRequestBusinessException;

public interface SquadRequestRepository {

    SquadRequest save(SquadRequest squadRequest);

    SquadRequest saveAndFlush(SquadRequest squadRequest);

    Optional<SquadRequest> findById(Long id);

    Optional<SquadRequest> findByIdWithSquad(Long id);

    Optional<SquadRequest> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    List<SquadRequestWithSquadAndCrewDomainDto> findSquadParticipantRequestsByMemberId(Long memberId);

    Page<SquadRequestDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable);

    void deleteById(Long id);

    void deleteBySquadIdCrewMemberId(Long squadId, Long crewMemberId);

    default SquadRequest getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new SquadRequestBusinessException.NeverRequested(NEVER_REQUESTED));
    }

    default SquadRequest getByIdWithSquad(Long id) {
        return findByIdWithSquad(id)
                .orElseThrow(() -> new SquadRequestBusinessException.NeverRequested(NEVER_REQUESTED));
    }

    default SquadRequest getBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return findBySquadIdAndCrewMemberId(squadId, crewMemberId)
                .orElseThrow(() -> new SquadRequestBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
