package revi1337.onsquad.squad_participant.presentation;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_participant.application.SquadParticipantService;
import revi1337.onsquad.squad_participant.presentation.dto.SimpleSquadParticipantResponse;
import revi1337.onsquad.squad_participant.presentation.dto.SquadParticipantRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadParticipantController {

    private final SquadParticipantService squadParticipantService;

    @GetMapping("/my/squad/requests")
    public ResponseEntity<RestResponse<List<SquadParticipantRequestResponse>>> fetchAllSquadRequests(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SquadParticipantRequestResponse> requestResponses = squadParticipantService
                .fetchAllSquadRequests(authenticatedMember.toDto().getId()).stream()
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

    @GetMapping("/my/squad/manage/requests")
    public void findRequestsInMySquad(
            @RequestParam @Positive Long crewId,
            @RequestParam @Positive Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        squadParticipantService.findRequestsInMySquad(authenticatedMember.toDto().getId(), crewId, squadId);
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<List<SimpleSquadParticipantResponse>>> fetchRequestsInSquad(
            @Authenticate AuthenticatedMember authenticatedMember,
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PageableDefault Pageable pageable
    ) {
        List<SimpleSquadParticipantResponse> simpleSquadParticipantResponses = squadParticipantService
                .fetchRequestsInSquad(authenticatedMember.toDto().getId(), crewId, squadId, pageable).stream()
                .map(SimpleSquadParticipantResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(simpleSquadParticipantResponses));
    }
}
