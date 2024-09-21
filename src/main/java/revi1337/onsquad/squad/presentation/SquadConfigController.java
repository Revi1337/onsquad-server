package revi1337.onsquad.squad.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadConfigService;
import revi1337.onsquad.squad.presentation.dto.request.SquadAcceptRequest;

@RequiredArgsConstructor
@RequestMapping("/api/v1/config")
@RestController
public class SquadConfigController {

    private final SquadConfigService squadConfigService;

    @PatchMapping("/squad/accept")
    public ResponseEntity<RestResponse<String>> acceptCrewMember(
            @Valid @RequestBody SquadAcceptRequest squadAcceptRequest,
            @Authenticate AuthenticatedMember ignored
    ) {
        squadConfigService.acceptSquadMember(squadAcceptRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
