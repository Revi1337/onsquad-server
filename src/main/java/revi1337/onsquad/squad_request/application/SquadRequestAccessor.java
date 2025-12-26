package revi1337.onsquad.squad_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_request.domain.SquadRequests;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;
import revi1337.onsquad.squad_request.domain.result.SquadRequestResult;
import revi1337.onsquad.squad_request.error.SquadRequestBusinessException;
import revi1337.onsquad.squad_request.error.SquadRequestErrorCode;

@RequiredArgsConstructor
@Component
public class SquadRequestAccessor {

    private final SquadRequestRepository squadRequestRepository;

    public SquadRequest getById(Long requestId) {
        return squadRequestRepository.findById(requestId)
                .orElseThrow(() -> new SquadRequestBusinessException.NotFound(SquadRequestErrorCode.NOT_FOUND));
    }

    public SquadRequests fetchMyRequests(Long memberId) {
        return new SquadRequests(squadRequestRepository.fetchMyRequests(memberId));
    }

    public List<SquadRequestResult> fetchAllBySquadId(Long squadId, Pageable pageable) {
        return squadRequestRepository.fetchAllBySquadId(squadId, pageable);
    }

    public boolean isRequestAbsent(Long memberId, Long squadId) {
        return squadRequestRepository.findBySquadIdAndMemberId(squadId, memberId).isEmpty();
    }
}
