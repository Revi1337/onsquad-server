package revi1337.onsquad.crew_member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.presentation.dto.response.EnrolledCrewResponse;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.presentation.dto.response.CrewMemberResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    @GetMapping("/my/crews")
    public ResponseEntity<RestResponse<List<EnrolledCrewResponse>>> findOwnedCrews(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<EnrolledCrewResponse> ownedCrewResponses = crewMemberService.findOwnedCrews(authenticatedMember.toDto().getId())
                .stream()
                .map(EnrolledCrewResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(ownedCrewResponses));
    }

    @GetMapping("/manage/crew/members")
    public ResponseEntity<RestResponse<List<CrewMemberResponse>>> findCrewMembers(
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<CrewMemberResponse> crewMemberResponse = crewMemberService.findCrewMembers(authenticatedMember.toDto().getId(), crewName)
                .stream()
                .map(CrewMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewMemberResponse));
    }
}
