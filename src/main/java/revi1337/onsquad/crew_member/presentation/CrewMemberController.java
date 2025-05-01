package revi1337.onsquad.crew_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.presentation.dto.response.CrewMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    @GetMapping("/crews/{crewId}/members")
    public ResponseEntity<RestResponse<List<CrewMemberResponse>>> fetchCrewMembers(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<CrewMemberResponse> crewMemberResponse = crewMemberService
                .fetchCrewMembers(authMemberAttribute.id(), crewId).stream()
                .map(CrewMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewMemberResponse));
    }
}
