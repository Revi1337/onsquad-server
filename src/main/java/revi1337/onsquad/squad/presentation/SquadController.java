package revi1337.onsquad.squad.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadService;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.squad.presentation.dto.request.SquadCreateRequest;
import revi1337.onsquad.squad.presentation.dto.request.SquadJoinRequest;
import revi1337.onsquad.squad.presentation.dto.response.SquadInfoResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadController {

    private final SquadService squadService;

    @PostMapping("/squad/new")
    public ResponseEntity<RestResponse<String>> createNewSquad(
            @Valid @RequestBody SquadCreateRequest squadCreateRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadService.createNewSquad(squadCreateRequest.toDto(), authenticatedMember.toDto().getId());

        return ResponseEntity.ok(RestResponse.created());
    }

    @PostMapping("/squad/join")
    public ResponseEntity<RestResponse<String>> joinSquad(
            @Valid @RequestBody SquadJoinRequest joinRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadService.submitParticipationRequest(joinRequest.toDto(), authenticatedMember.toDto().getId());

        return ResponseEntity.ok(RestResponse.noContent());
    }

    @GetMapping("/squad")
    public ResponseEntity<RestResponse<SquadInfoResponse>> findSquad(
            @RequestParam Long id
    ) {
        SquadInfoResponse squadResponse = SquadInfoResponse.from(squadService.findSquad(id));

        return ResponseEntity.ok(RestResponse.success(squadResponse));
    }

    @GetMapping("/squads")
    public ResponseEntity<RestResponse<List<SquadInfoResponse>>> findSquads(
            @RequestParam String crewName,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable
    ) {
        List<SquadInfoResponse> squadResponses = squadService.findSquads(crewName, category, pageable).stream()
                .map(SquadInfoResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(RestResponse.success(squadResponses));
    }
}
