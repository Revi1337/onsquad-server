package revi1337.onsquad.crew_member.application.leaderboard;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.application.response.CrewRankedMemberResponse;

@Service
@RequiredArgsConstructor
public class CrewRankedMemberQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewRankedMemberCacheService crewRankedMemberCacheService;

    public List<CrewRankedMemberResponse> findRankedMembers(Long memberId, Long crewId) {
        crewMemberAccessor.validateMemberInCrew(memberId, crewId);
        return crewRankedMemberCacheService.findAllByCrewId(crewId).stream()
                .map(CrewRankedMemberResponse::from)
                .toList();
    }
}
