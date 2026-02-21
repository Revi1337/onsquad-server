package revi1337.onsquad.crew_member.application.leaderboard;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.application.response.CrewRankerResponse;

@Service
@RequiredArgsConstructor
public class CrewRankerQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewRankerCacheService crewRankerCacheService;

    public List<CrewRankerResponse> findCrewRankers(Long memberId, Long crewId) {
        crewMemberAccessor.validateMemberInCrew(memberId, crewId);
        return crewRankerCacheService.findAllByCrewId(crewId);
    }
}
