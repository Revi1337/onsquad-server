package revi1337.onsquad.squad_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.result.SquadRequestResult;

public interface SquadRequestRepository {

    SquadRequest save(SquadRequest squadRequest);

    SquadRequest saveAndFlush(SquadRequest squadRequest);

    Optional<SquadRequest> findById(Long id);

    Optional<SquadRequest> findBySquadIdAndMemberId(Long squadId, Long memberId);

    List<SquadRequestResult> fetchAllBySquadId(Long squadId, Pageable pageable);

    List<SquadRequest> fetchMyRequests(Long memberId);

    void deleteById(Long id);

    void deleteBySquadIdMemberId(Long squadId, Long memberId);

}
