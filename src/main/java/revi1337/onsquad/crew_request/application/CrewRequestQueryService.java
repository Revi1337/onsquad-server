package revi1337.onsquad.crew_request.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_request.application.response.CrewRequestResponse;
import revi1337.onsquad.crew_request.application.response.CrewRequestWithCrewResponse;
import revi1337.onsquad.crew_request.domain.CrewRequestPolicy;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewRequestQueryService {

    private final CrewRequestAccessor crewRequestAccessor;
    private final CrewMemberAccessor crewMemberAccessor;

    public PageResponse<CrewRequestResponse> fetchAllRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewRequestPolicy.ensureReadRequests(me);

        Page<CrewRequestResponse> response = crewRequestAccessor.fetchCrewRequests(crewId, pageable)
                .map(CrewRequestResponse::from);

        return PageResponse.from(response);
    }

    public PageResponse<CrewRequestWithCrewResponse> fetchAllCrewRequests(Long memberId, Pageable pageable) {
        Page<CrewRequestWithCrewResponse> response = crewRequestAccessor.fetchAllWithSimpleCrewByMemberId(memberId, pageable)
                .map(CrewRequestWithCrewResponse::from);

        return PageResponse.from(response);
    }
}
