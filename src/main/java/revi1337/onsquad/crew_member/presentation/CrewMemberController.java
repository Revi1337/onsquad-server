package revi1337.onsquad.crew_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.presentation.dto.response.CrewMemberResponse;
import revi1337.onsquad.crew_member.presentation.dto.response.EnrolledCrewResponse;
import revi1337.onsquad.crew_member.presentation.dto.response.Top5CrewMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    @GetMapping("/my/crews")
    public ResponseEntity<RestResponse<List<EnrolledCrewResponse>>> fetchAllJoinedCrews(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<EnrolledCrewResponse> ownedCrewResponses = crewMemberService
                .fetchAllJoinedCrews(authenticatedMember.toDto().getId())
                .stream()
                .map(EnrolledCrewResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(ownedCrewResponses));
    }

    @GetMapping("/crews/{crewId}/top")
    public ResponseEntity<RestResponse<List<Top5CrewMemberResponse>>> findTop5CrewMembers(
            @PathVariable Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<Top5CrewMemberResponse> top5CrewMembers = crewMemberService
                .findTop5CrewMembers(authenticatedMember.toDto().getId(), crewId).stream()
                .map(Top5CrewMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(top5CrewMembers));
    }

    @GetMapping("/crews/{crewId}/members")
    public ResponseEntity<RestResponse<List<CrewMemberResponse>>> findCrewMembers(
            @PathVariable Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<CrewMemberResponse> crewMemberResponse = crewMemberService
                .findCrewMembers(authenticatedMember.toDto().getId(), crewId)
                .stream()
                .map(CrewMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewMemberResponse));
    }
}
