package revi1337.onsquad.crew_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithCrewResult;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithMemberResult;
import revi1337.onsquad.crew_request.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.error.CrewRequestErrorCode;

@RequiredArgsConstructor
@Component
public class CrewRequestAccessor {

    private final CrewRequestRepository crewRequestRepository;

    public CrewRequest getById(Long requestId) {
        return crewRequestRepository.findById(requestId)
                .orElseThrow(() -> new CrewRequestBusinessException.NotFound(CrewRequestErrorCode.NOT_FOUND));
    }

    public Page<CrewRequestWithMemberResult> fetchCrewRequests(Long crewId, Pageable pageable) {
        return crewRequestRepository.fetchCrewRequests(crewId, pageable);
    }

    public List<CrewRequestWithCrewResult> fetchAllWithSimpleCrewByMemberId(Long memberId) {
        return crewRequestRepository.fetchAllWithSimpleCrewByMemberId(memberId);
    }

    public boolean isRequestAbsent(Long memberId, Long crewId) {
        return crewRequestRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty();
    }
}
