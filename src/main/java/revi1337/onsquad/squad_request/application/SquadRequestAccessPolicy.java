package revi1337.onsquad.squad_request.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;
import revi1337.onsquad.squad_request.error.SquadRequestErrorCode;
import revi1337.onsquad.squad_request.error.exception.SquadRequestBusinessException;

@RequiredArgsConstructor
@Component
public class SquadRequestAccessPolicy {

    private final SquadRequestRepository squadRequestRepository;

    public SquadRequest ensureRequestExistsAndGet(Long requestId) {
        return squadRequestRepository.findById(requestId)
                .orElseThrow(() -> new SquadRequestBusinessException.NotFound(SquadRequestErrorCode.NOT_FOUND));
    }

    public boolean isRequestAbsent(Long memberId, Long squadId) {
        return squadRequestRepository.findBySquadIdAndMemberId(squadId, memberId).isEmpty();
    }

    public void ensureMatchSquad(SquadRequest request, Long squadId) {
        if (request.mismatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
    }

    public void ensureAcceptable(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public void ensureRejectable(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }

    public void ensureRequestListAccessible(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }
}
