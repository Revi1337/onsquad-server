package revi1337.onsquad.crew_participant.presentation;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_participant.application.CrewParticipantService;
import revi1337.onsquad.crew_participant.presentation.dto.response.CrewRequestWithCrewResponse;
import revi1337.onsquad.crew_participant.presentation.dto.response.CrewRequestWithMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewParticipantController {

    private final CrewParticipantService crewParticipantService;

    @PostMapping("/crews/{crewId}/requests")
    public ResponseEntity<RestResponse<String>> requestInCrew(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewParticipantService.requestInCrew(authMemberAttribute.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PatchMapping("/crews/{crewId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> acceptCrewRequests(
            @PathVariable Long crewId,
            @PathVariable Long requestId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewParticipantService.acceptCrewRequest(authMemberAttribute.id(), crewId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/crews/{crewId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> rejectCrewRequest(
            @PathVariable Long crewId,
            @PathVariable Long requestId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewParticipantService.rejectCrewRequest(authMemberAttribute.id(), crewId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crews/{crewId}/requests")
    public ResponseEntity<RestResponse<List<CrewRequestWithMemberResponse>>> fetchCrewRequests(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<CrewRequestWithMemberResponse> requestResponses = crewParticipantService
                .fetchCrewRequests(authMemberAttribute.id(), crewId, pageable).stream()
                .map(CrewRequestWithMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }

    @DeleteMapping("/crews/{crewId}/requests/me")
    public ResponseEntity<RestResponse<String>> cancelCrewRequest(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewParticipantService.cancelCrewRequest(authMemberAttribute.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crew-requests/me")
    public ResponseEntity<RestResponse<List<CrewRequestWithCrewResponse>>> fetchAllCrewRequests(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<CrewRequestWithCrewResponse> crewRequestWithCrewResponse = crewParticipantService
                .fetchAllCrewRequests(authMemberAttribute.id()).stream()
                .map(CrewRequestWithCrewResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewRequestWithCrewResponse));
    }
}
