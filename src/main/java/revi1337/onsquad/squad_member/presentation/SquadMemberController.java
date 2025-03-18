package revi1337.onsquad.squad_member.presentation;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_member.application.SquadMemberService;
import revi1337.onsquad.squad_member.presentation.dto.response.EnrolledSquadResponse;
import revi1337.onsquad.squad_member.presentation.dto.response.SquadInMembersResponse;
import revi1337.onsquad.squad_member.presentation.dto.response.SquadMembersWithSquadResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadMemberController {

    private final SquadMemberService squadMemberService;

    @GetMapping("/my/squads")
    public ResponseEntity<RestResponse<List<EnrolledSquadResponse>>> fetchAllJoinedSquads(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<EnrolledSquadResponse> enrolledSquadResponses = squadMemberService
                .fetchAllJoinedSquads(authenticatedMember.toDto().getId()).stream()
                .map(EnrolledSquadResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(enrolledSquadResponses));
    }

    @GetMapping("/manage/squad/members")
    public ResponseEntity<RestResponse<SquadMembersWithSquadResponse>> findSquadWithMembers(
            @Authenticate AuthenticatedMember authenticatedMember,
            @RequestParam @Positive Long crewId,
            @RequestParam Long squadId
    ) {
        SquadMembersWithSquadResponse squadWithSquadMembers = SquadMembersWithSquadResponse.from(
                squadMemberService.findSquadWithMembers(authenticatedMember.toDto().getId(), crewId, squadId)
        );

        return ResponseEntity.ok().body(RestResponse.success(squadWithSquadMembers));
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/members")
    public ResponseEntity<RestResponse<SquadInMembersResponse>> fetchMembersInSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        SquadInMembersResponse squadInMembersResponse = SquadInMembersResponse.from(
                squadMemberService.fetchMembersInSquad(authenticatedMember.toDto().getId(), crewId, squadId)
        );

        return ResponseEntity.ok().body(RestResponse.success(squadInMembersResponse));
    }
}
