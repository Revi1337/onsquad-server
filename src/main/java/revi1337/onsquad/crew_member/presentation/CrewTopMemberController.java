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
import revi1337.onsquad.crew_member.application.CrewTopMemberService;
import revi1337.onsquad.crew_member.application.response.Top5CrewMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewTopMemberController {

    private final CrewTopMemberService crewTopMemberService;

    @GetMapping("/crews/{crewId}/members/top")
    public ResponseEntity<RestResponse<List<Top5CrewMemberResponse>>> findTop5CrewMembers(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        List<Top5CrewMemberResponse> response = crewTopMemberService.findTop5CrewMembers(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }
}
