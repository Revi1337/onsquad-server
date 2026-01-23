package revi1337.onsquad.crew_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_request.application.response.CrewRequestWithCrewResponse;
import revi1337.onsquad.crew_request.application.response.CrewRequestWithMemberResponse;
import revi1337.onsquad.crew_request.domain.CrewRequestPolicy;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewRequestQueryService {

    private final CrewRequestAccessor crewRequestAccessor;
    private final CrewMemberAccessor crewMemberAccessor;

    public List<CrewRequestWithMemberResponse> fetchAllRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewRequestPolicy.ensureReadRequests(me);

        return crewRequestAccessor.fetchCrewRequests(crewId, pageable).stream()
                .map(CrewRequestWithMemberResponse::from)
                .toList();
    }

    public List<CrewRequestWithCrewResponse> fetchAllCrewRequests(Long memberId) {
        return crewRequestAccessor.fetchAllWithSimpleCrewByMemberId(memberId).stream()
                .map(CrewRequestWithCrewResponse::from)
                .toList();
    }
}
