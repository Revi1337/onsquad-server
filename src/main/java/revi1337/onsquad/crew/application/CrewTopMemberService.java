package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew.application.dto.response.Top5CrewMemberResponse;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberCacheRepository;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;

@RequiredArgsConstructor
@Service
public class CrewTopMemberService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewTopMemberCacheRepository crewTopMemberCacheRepository;

    public List<Top5CrewMemberResponse> findTop5CrewMembers(Long memberId, Long crewId) {
        crewMemberAccessPolicy.ensureMemberInCrew(memberId, crewId);
        return crewTopMemberCacheRepository.findAllByCrewId(crewId).stream()
                .map(Top5CrewMemberResponse::from)
                .toList();
    }
}
