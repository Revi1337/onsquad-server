package revi1337.onsquad.squad_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_member.application.SquadMemberCommandService;
import revi1337.onsquad.squad_member.application.SquadMemberQueryService;
import revi1337.onsquad.squad_member.presentation.dto.response.EnrolledSquadResponse;
import revi1337.onsquad.squad_member.presentation.dto.response.SquadMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadMemberController {

    private final SquadMemberCommandService squadMemberCommandService;
    private final SquadMemberQueryService squadMemberQueryService;

    @GetMapping("/my/squads")
    public ResponseEntity<RestResponse<List<EnrolledSquadResponse>>> fetchAllJoinedSquads(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<EnrolledSquadResponse> enrolledSquadResponses = squadMemberQueryService
                .fetchAllJoinedSquads(authMemberAttribute.id()).stream()
                .map(EnrolledSquadResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(enrolledSquadResponses));
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/members")
    public ResponseEntity<RestResponse<List<SquadMemberResponse>>> fetchMembers(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SquadMemberResponse> results = squadMemberQueryService
                .fetchAllBySquadId(authMemberAttribute.id(), crewId, squadId).stream()
                .map(SquadMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(results));
    }

    @DeleteMapping("/crews/{crewId}/squads/{squadId}/me")
    public ResponseEntity<RestResponse<String>> leaveSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadMemberCommandService.leaveSquad(authMemberAttribute.id(), crewId, squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
