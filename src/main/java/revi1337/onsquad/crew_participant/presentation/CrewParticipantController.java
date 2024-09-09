package revi1337.onsquad.crew_participant.presentation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_participant.application.CrewParticipantService;
import revi1337.onsquad.crew_participant.presentation.dto.response.CrewParticipantRequestResponse;
import revi1337.onsquad.crew_participant.presentation.dto.response.SimpleCrewParticipantRequestResponse;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CrewParticipantController {

    private final CrewParticipantService crewParticipantService;

    @GetMapping("/my/crew/requests")
    public ResponseEntity<RestResponse<List<CrewParticipantRequestResponse>>> findMyCrewRequests(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<CrewParticipantRequestResponse> crewParticipantRequestResponse = crewParticipantService.findMyCrewRequests(authenticatedMember.toDto().getId()).stream()
                .map(CrewParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewParticipantRequestResponse));
    }

    @DeleteMapping("/my/crew/request")
    public ResponseEntity<RestResponse<String>> modifyCrewRequest(
            @RequestParam @Positive Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewParticipantService.rejectCrewRequest(authenticatedMember.toDto().getId(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/manage/crew/requests")
    public ResponseEntity<RestResponse<List<SimpleCrewParticipantRequestResponse>>> findMyCrew(
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SimpleCrewParticipantRequestResponse> requestResponses = crewParticipantService.findCrewRequestsInMyCrew(authenticatedMember.toDto().getId(), crewName).stream()
                .map(SimpleCrewParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }
}
