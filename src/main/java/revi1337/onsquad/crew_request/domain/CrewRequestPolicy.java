package revi1337.onsquad.crew_request.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.error.CrewRequestErrorCode;

@RequiredArgsConstructor
public class CrewRequestPolicy {

    public static void ensureMatchCrew(CrewRequest request, Long crewId) {
        if (request.mismatchCrewId(crewId)) {
            throw new CrewRequestBusinessException.MismatchReference(CrewRequestErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public static void ensureRequestListAccessible(CrewMember me) {
        if (me.isLowerThanManager()) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }

    public static void ensureAcceptable(CrewMember me) {
        if (me.isLowerThanManager()) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public static void ensureRejectable(CrewMember me) {
        if (me.isLowerThanManager()) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }
}
