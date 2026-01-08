package revi1337.onsquad.squad_request.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.error.SquadRequestBusinessException;
import revi1337.onsquad.squad_request.error.SquadRequestErrorCode;

@NoArgsConstructor(access = PRIVATE)
public class SquadRequestPolicy {

    public static void ensureMatchSquad(SquadRequest request, Long squadId) {
        if (request.mismatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
    }

    public static void ensureAcceptable(SquadMember me) {
        if (SquadMemberPolicy.isNotLeader(me)) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public static void ensureRejectable(SquadMember me) {
        if (SquadMemberPolicy.isNotLeader(me)) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }

    public static void ensureRequestListAccessible(SquadMember me) {
        if (SquadMemberPolicy.isNotLeader(me)) {
            throw new SquadRequestBusinessException.InsufficientAuthority(SquadRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }
}
