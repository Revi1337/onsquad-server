package revi1337.onsquad.crew_request.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.domain.error.CrewRequestErrorCode;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;

@Component
@RequiredArgsConstructor
public class CrewRequestAccessor {

    private final CrewRequestRepository crewRequestRepository;

    public CrewRequest getById(Long requestId) {
        return crewRequestRepository.findById(requestId)
                .orElseThrow(() -> new CrewRequestBusinessException.NotFound(CrewRequestErrorCode.NOT_FOUND));
    }

    public Page<CrewRequest> fetchCrewRequests(Long crewId, Pageable pageable) {
        return crewRequestRepository.fetchCrewRequests(crewId, pageable);
    }

    public Page<CrewRequest> fetchAllWithSimpleCrewByMemberId(Long memberId, Pageable pageable) {
        return crewRequestRepository.fetchAllWithSimpleCrewByMemberId(memberId, pageable);
    }

    public boolean checkAlreadyRequest(Long memberId, Long crewId) {
        return crewRequestRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent();
    }

    public boolean isRequestAbsent(Long memberId, Long crewId) {
        return crewRequestRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty();
    }
}
