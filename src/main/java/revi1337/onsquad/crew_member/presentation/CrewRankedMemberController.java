package revi1337.onsquad.crew_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.application.leaderboard.CrewRankedMemberQueryService;
import revi1337.onsquad.crew_member.application.response.CrewRankedMemberResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CrewRankedMemberController {

    private final CrewRankedMemberQueryService crewRankedMemberQueryService;

    @GetMapping("/crews/{crewId}/members/ranker")
    public ResponseEntity<RestResponse<List<CrewRankedMemberResponse>>> findCrewRankedMembers(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        List<CrewRankedMemberResponse> response = crewRankedMemberQueryService.findRankedMembers(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }
}
