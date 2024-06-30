package revi1337.onsquad.squad.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadService;
import revi1337.onsquad.squad.dto.request.SquadCreateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/v1/squad")
@RestController
public class SquadController {

    private final SquadService squadService;

    @PostMapping("/new")
    public ResponseEntity<RestResponse<String>> createNewSquad(
            @Valid @RequestBody SquadCreateRequest squadCreateRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadService.createNewSquad(squadCreateRequest.toDto(authenticatedMember.toDto()));

        return ResponseEntity.ok(RestResponse.created());
    }
}
