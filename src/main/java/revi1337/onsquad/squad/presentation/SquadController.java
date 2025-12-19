package revi1337.onsquad.squad.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.category.presentation.request.CategoryCondition;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.application.SquadQueryService;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithParticipantAndLeaderAndViewStateResponse;
import revi1337.onsquad.squad.presentation.request.SquadCreateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadController {

    private final SquadCommandService squadCommandService;
    private final SquadQueryService squadQueryService;

    @PostMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<Void>> newSquad(
            @PathVariable Long crewId,
            @Valid @RequestBody SquadCreateRequest squadCreateRequest,
            @Authenticate CurrentMember currentMember
    ) {
        squadCommandService.newSquad(currentMember.id(), crewId, squadCreateRequest.toDto());

        return ResponseEntity.ok(RestResponse.created());
    }

    @GetMapping("/{crewId}/squads/{squadId}")
    public ResponseEntity<RestResponse<SquadWithParticipantAndLeaderAndViewStateResponse>> fetchSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        SquadWithParticipantAndLeaderAndViewStateResponse response = squadQueryService.fetchSquad(currentMember.id(), crewId, squadId);

        return ResponseEntity.ok(RestResponse.success(response));
    }

    @GetMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<List<SquadResponse>>> fetchSquads(
            @PathVariable Long crewId,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadResponse> response = squadQueryService.fetchSquads(currentMember.id(), crewId, category, pageable);

        return ResponseEntity.ok(RestResponse.success(response));
    }

    @GetMapping("/{crewId}/manage/squads")
    public ResponseEntity<RestResponse<List<SquadWithLeaderStateResponse>>> fetchSquadsWithOwnerState(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadWithLeaderStateResponse> response = squadQueryService.fetchSquadsWithOwnerState(currentMember.id(), crewId, pageable);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @DeleteMapping("/{crewId}/squads/{squadId}")
    public ResponseEntity<RestResponse<Void>> deleteSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        squadCommandService.deleteSquad(currentMember.id(), crewId, squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
