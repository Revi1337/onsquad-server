package revi1337.onsquad.squad_member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_member.presentation.dto.response.EnrolledSquadResponse;
import revi1337.onsquad.squad_member.application.SquadMemberService;
import revi1337.onsquad.squad_member.presentation.dto.response.SquadWithMemberResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadMemberController {

    private final SquadMemberService squadMemberService;

    @GetMapping("/my/squads")
    public ResponseEntity<RestResponse<List<EnrolledSquadResponse>>> findEnrolledSquads(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<EnrolledSquadResponse> enrolledSquadResponses = squadMemberService.findEnrolledSquads(authenticatedMember.toDto().getId()).stream()
                .map(EnrolledSquadResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(enrolledSquadResponses));
    }

    @GetMapping("/manage/squad/members")
    public ResponseEntity<RestResponse<SquadWithMemberResponse>> findSquadWithMembers(
            @RequestParam String crewName,
            @RequestParam Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        SquadWithMemberResponse squadWithSquadMembers = SquadWithMemberResponse.from(
                squadMemberService.findSquadWithMembers(authenticatedMember.toDto().getId(), crewName, squadId)
        );

        return ResponseEntity.ok().body(RestResponse.success(squadWithSquadMembers));
    }
}
