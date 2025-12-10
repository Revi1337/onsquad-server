package revi1337.onsquad.squad_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadAndCrewDomainDto;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public interface SquadRequestRepository {

    SquadRequest save(SquadRequest squadRequest);

    SquadRequest saveAndFlush(SquadRequest squadRequest);

    Optional<SquadRequest> findById(Long id);

    Optional<SquadRequest> findByIdWithSquad(Long id);

    Optional<SquadRequest> findBySquadIdAndMemberId(Long squadId, Long memberId);

    List<SquadRequestWithSquadAndCrewDomainDto> findSquadParticipantRequestsByMemberId(Long memberId);

    Page<SquadRequestDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable);

    void deleteById(Long id);

    void deleteBySquadIdMemberId(Long squadId, Long memberId);

}
