package revi1337.onsquad.crew_participant.presentation;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_participant.application.CrewParticipantService;
import revi1337.onsquad.crew_participant.presentation.dto.response.CrewParticipantRequestResponse;
import revi1337.onsquad.crew_participant.presentation.dto.response.SimpleCrewParticipantRequestResponse;

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
        List<CrewParticipantRequestResponse> crewParticipantRequestResponse = crewParticipantService
                .findMyCrewRequests(authenticatedMember.toDto().getId()).stream()
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
    public ResponseEntity<RestResponse<List<SimpleCrewParticipantRequestResponse>>> fetchCrewRequests(
            @Authenticate AuthenticatedMember authenticatedMember,
            @RequestParam @Positive Long crewId,
            @PageableDefault Pageable pageable
    ) {
        List<SimpleCrewParticipantRequestResponse> requestResponses = crewParticipantService
                .fetchCrewRequests(authenticatedMember.toDto().getId(), crewId, pageable).stream()
                .map(SimpleCrewParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }
}
