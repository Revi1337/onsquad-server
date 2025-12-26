package revi1337.onsquad.squad_request.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.error.SquadRequestBusinessException;
import revi1337.onsquad.squad_request.error.SquadRequestErrorCode;

@RequiredArgsConstructor
public class SquadRequestPolicy {

    public static void ensureMatchSquad(SquadRequest request, Long squadId) {
        if (request.mismatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
    }

    public static void ensureAcceptable(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public static void ensureRejectable(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }

    public static void ensureRequestListAccessible(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }
}
