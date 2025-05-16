package revi1337.onsquad.squad.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.application.SquadQueryService;
import revi1337.onsquad.squad.presentation.dto.request.SquadCreateRequest;
import revi1337.onsquad.squad.presentation.dto.response.SquadResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadWithParticipantAndLeaderAndViewStateResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadController {

    private final SquadCommandService squadCommandService;
    private final SquadQueryService squadQueryService;

    @PostMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<String>> newSquad(
            @PathVariable Long crewId,
            @Valid @RequestBody SquadCreateRequest squadCreateRequest,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadCommandService.newSquad(authMemberAttribute.id(), crewId, squadCreateRequest.toDto());

        return ResponseEntity.ok(RestResponse.created());
    }

    @GetMapping("/{crewId}/squads/{squadId}")
    public ResponseEntity<RestResponse<SquadWithParticipantAndLeaderAndViewStateResponse>> fetchSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        SquadWithParticipantAndLeaderAndViewStateResponse squadResponse = SquadWithParticipantAndLeaderAndViewStateResponse.from(
                squadQueryService.fetchSquad(authMemberAttribute.id(), crewId, squadId)
        );

        return ResponseEntity.ok(RestResponse.success(squadResponse));
    }

    @GetMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<List<SquadResponse>>> fetchSquads(
            @PathVariable Long crewId,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable
    ) {
        List<SquadResponse> squadResponses = squadQueryService.fetchSquads(crewId, category, pageable).stream()
                .map(SquadResponse::from)
                .toList();

        return ResponseEntity.ok(RestResponse.success(squadResponses));
    }

    @GetMapping("/{crewId}/manage/squads")
    public ResponseEntity<RestResponse<List<SquadWithLeaderStateResponse>>> fetchSquadsWithOwnerState(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SquadWithLeaderStateResponse> squadResponses = squadQueryService
                .fetchSquadsWithOwnerState(authMemberAttribute.id(), crewId, pageable).stream()
                .map(SquadWithLeaderStateResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(squadResponses));
    }
}
