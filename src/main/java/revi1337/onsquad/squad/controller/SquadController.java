package revi1337.onsquad.squad.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadService;
import revi1337.onsquad.squad.dto.request.SquadCreateRequest;
import revi1337.onsquad.squad.dto.request.SquadJoinRequest;
import revi1337.onsquad.squad.dto.response.SquadResponse;

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
        squadService.joinSquad(joinRequest.toDto(), authenticatedMember.toDto().getId());

        return ResponseEntity.ok(RestResponse.noContent());
    }

    @GetMapping("/squad")
    public ResponseEntity<RestResponse<SquadResponse>> findSquad(
            @RequestParam Long id,
            @RequestParam String title
    ) {
        SquadResponse squadResponse = SquadResponse.from(squadService.findSquad(id, title));

        return ResponseEntity.ok(RestResponse.success(squadResponse));
    }
}
