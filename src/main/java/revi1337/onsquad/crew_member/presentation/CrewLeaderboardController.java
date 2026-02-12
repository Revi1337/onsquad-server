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
import revi1337.onsquad.crew_member.application.leaderboard.CrewRankerQueryService;
import revi1337.onsquad.crew_member.application.response.CrewRankerResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CrewLeaderboardController {

    private final CrewRankerQueryService crewRankerQueryService;

    @GetMapping("/crews/{crewId}/leaderboard")
    public ResponseEntity<RestResponse<List<CrewRankerResponse>>> getLeaderboard(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        List<CrewRankerResponse> response = crewRankerQueryService.findCrewRankers(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }
}
