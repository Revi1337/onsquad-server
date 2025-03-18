package revi1337.onsquad.squad_participant.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.presentation.dto.request.SquadAcceptRequest;
import revi1337.onsquad.squad_participant.application.SquadParticipantService;
import revi1337.onsquad.squad_participant.presentation.dto.SimpleSquadParticipantResponse;
import revi1337.onsquad.squad_participant.presentation.dto.SquadParticipantRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadParticipantController {

    private final SquadParticipantService squadParticipantService;

    @GetMapping("/my/squads/requests")
    public ResponseEntity<RestResponse<List<SquadParticipantRequestResponse>>> fetchAllSquadRequests(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SquadParticipantRequestResponse> requestResponses = squadParticipantService
                .fetchAllSquadRequests(authenticatedMember.toDto().getId()).stream()
                .map(SquadParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }

    @DeleteMapping("/my/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<String>> rejectSquadRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadParticipantService.rejectSquadRequest(authenticatedMember.toDto().getId(), crewId, squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PostMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<String>> requestInSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadParticipantService.requestInSquad(authenticatedMember.toDto().getId(), crewId, squadId);

        return ResponseEntity.ok(RestResponse.noContent());
    }

    @PatchMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<String>> acceptSquadRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Valid @RequestBody SquadAcceptRequest squadAcceptRequest,
            @Authenticate AuthenticatedMember ignored
    ) {
        squadParticipantService.acceptCrewRequest(crewId, squadId, squadAcceptRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<List<SimpleSquadParticipantResponse>>> fetchRequestsInSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SimpleSquadParticipantResponse> simpleSquadParticipantResponses = squadParticipantService
                .fetchRequestsInSquad(authenticatedMember.toDto().getId(), crewId, squadId, pageable).stream()
                .map(SimpleSquadParticipantResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(simpleSquadParticipantResponses));
    }
}
