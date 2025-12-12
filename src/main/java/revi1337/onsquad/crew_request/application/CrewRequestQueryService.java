package revi1337.onsquad.crew_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_request.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_request.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrewRequestQueryService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewRequestAccessPolicy crewRequestAccessPolicy;
    private final CrewRequestRepository crewRequestRepository;

    public List<CrewRequestWithMemberDto> fetchAllRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewRequestAccessPolicy.ensureRequestListAccessible(crewMember);
        return crewRequestRepository.fetchCrewRequests(crewId, pageable).stream()
                .map(CrewRequestWithMemberDto::from)
                .toList();
    }

    public List<CrewRequestWithCrewDto> fetchAllCrewRequests(Long memberId) {
        return crewRequestRepository.fetchAllWithSimpleCrewByMemberId(memberId).stream()
                .map(CrewRequestWithCrewDto::from)
                .toList();
    }
}
