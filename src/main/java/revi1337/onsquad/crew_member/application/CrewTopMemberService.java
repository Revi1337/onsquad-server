package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.application.response.Top5CrewMemberResponse;

@RequiredArgsConstructor
@Service
public class CrewTopMemberService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewTopMemberCacheService crewTopMemberCacheService;

    public List<Top5CrewMemberResponse> findTop5CrewMembers(Long memberId, Long crewId) {
        crewMemberAccessPolicy.ensureMemberInCrew(memberId, crewId);
        return crewTopMemberCacheService.findAllByCrewId(crewId).stream()
                .map(Top5CrewMemberResponse::from)
                .toList();
    }
}
