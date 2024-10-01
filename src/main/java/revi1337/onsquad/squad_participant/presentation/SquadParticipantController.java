package revi1337.onsquad.squad_participant.presentation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_participant.application.SquadParticipantService;
import revi1337.onsquad.squad_participant.presentation.dto.SquadParticipantRequestResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadParticipantController {

    private final SquadParticipantService squadParticipantService;

    @GetMapping("/my/squad/requests")
    public ResponseEntity<RestResponse<List<SquadParticipantRequestResponse>>> findSquadMemberRequests(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SquadParticipantRequestResponse> requestResponses = squadParticipantService.findSquadParticipants(authenticatedMember.toDto().getId()).stream()
                .map(SquadParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }

    @DeleteMapping("/my/squad/request")
    public ResponseEntity<RestResponse<String>> modifySquadRequest(
            @RequestParam @Positive Long crewId,
            @RequestParam @Positive Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadParticipantService.rejectSquadRequest(authenticatedMember.toDto().getId(), crewId, squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
