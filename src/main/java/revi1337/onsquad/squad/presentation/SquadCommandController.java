package revi1337.onsquad.squad.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.presentation.dto.request.SquadCreateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadCommandController {

    private final SquadCommandService squadCommandService;

    @PostMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<String>> createNewSquad(
            @PathVariable Long crewId,
            @Valid @RequestBody SquadCreateRequest squadCreateRequest,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadCommandService.createNewSquad(authMemberAttribute.id(), crewId, squadCreateRequest.toDto());

        return ResponseEntity.ok(RestResponse.created());
    }
}
