package revi1337.onsquad.crew_participant.presentation;

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
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.presentation.dto.request.CrewAcceptRequest;
import revi1337.onsquad.crew_participant.application.CrewParticipantService;
import revi1337.onsquad.crew_participant.presentation.dto.response.CrewParticipantRequestResponse;
import revi1337.onsquad.crew_participant.presentation.dto.response.SimpleCrewParticipantRequestResponse;

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

    @PatchMapping("/crews/{crewId}/requests")
    public ResponseEntity<RestResponse<String>> acceptCrewRequest(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewAcceptRequest crewAcceptRequest,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewParticipantService.acceptCrewRequest(authMemberAttribute.id(), crewId, crewAcceptRequest.memberId());

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
    public ResponseEntity<RestResponse<List<SimpleCrewParticipantRequestResponse>>> fetchCrewRequests(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SimpleCrewParticipantRequestResponse> requestResponses = crewParticipantService
                .fetchCrewRequests(authMemberAttribute.id(), crewId, pageable).stream()
                .map(SimpleCrewParticipantRequestResponse::from)
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

    @GetMapping("/crews/requests/me")
    public ResponseEntity<RestResponse<List<CrewParticipantRequestResponse>>> fetchAllCrewRequests(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<CrewParticipantRequestResponse> crewParticipantRequestResponse = crewParticipantService
                .fetchAllCrewRequests(authMemberAttribute.id()).stream()
                .map(CrewParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewParticipantRequestResponse));
    }
}
