package revi1337.onsquad.crew_request.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.crew_request.error.CrewRequestErrorCode;
import revi1337.onsquad.crew_request.error.exception.CrewRequestBusinessException;

@RequiredArgsConstructor
@Component
public class CrewRequestAccessPolicy {

    private final CrewRequestRepository crewRequestRepository;

    public CrewRequest ensureRequestExistsAndGet(Long requestId) {
        return crewRequestRepository.findById(requestId)
                .orElseThrow(() -> new CrewRequestBusinessException.NotFound(CrewRequestErrorCode.NOT_FOUND));
    }

    public boolean isRequestAbsent(Long memberId, Long crewId) {
        return crewRequestRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty();
    }

    public void ensureMatchCrew(CrewRequest request, Long crewId) {
        if (request.mismatchCrewId(crewId)) {
            throw new CrewRequestBusinessException.MismatchReference(CrewRequestErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public void ensureAcceptable(CrewMember crewMember) {
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public void ensureRejectable(CrewMember crewMember) {
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }

    public void ensureRequestListAccessible(CrewMember crewMember) {
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }
}
