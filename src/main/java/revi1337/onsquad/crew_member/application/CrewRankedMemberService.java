package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.application.response.CrewRankedMemberResponse;

@RequiredArgsConstructor
@Service
public class CrewRankedMemberService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewRankedMemberCacheService crewRankedMemberCacheService;

    public List<CrewRankedMemberResponse> findCrewRankedMembers(Long memberId, Long crewId) {
        crewMemberAccessor.validateMemberInCrew(memberId, crewId);
        return crewRankedMemberCacheService.findAllByCrewId(crewId).stream()
                .map(CrewRankedMemberResponse::from)
                .toList();
    }
}
