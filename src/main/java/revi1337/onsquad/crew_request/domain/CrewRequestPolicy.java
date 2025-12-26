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

    public static void ensureAcceptable(CrewMember crewMember) {
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public static void ensureRejectable(CrewMember crewMember) {
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }

    public static void ensureRequestListAccessible(CrewMember crewMember) {
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }
}
