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
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.presentation.dto.request.SquadAcceptRequest;
import revi1337.onsquad.squad_participant.application.SquadParticipantCommandService;
import revi1337.onsquad.squad_participant.application.SquadParticipantQueryService;
import revi1337.onsquad.squad_participant.presentation.dto.SimpleSquadParticipantResponse;
import revi1337.onsquad.squad_participant.presentation.dto.SquadParticipantRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadParticipantController {

    private final SquadParticipantCommandService squadParticipantCommandService;
    private final SquadParticipantQueryService squadParticipantQueryService;

    @PostMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<String>> request(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadParticipantCommandService.request(authMemberAttribute.id(), crewId, squadId);

        return ResponseEntity.ok(RestResponse.created());
    }

    @PatchMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<String>> acceptRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Valid @RequestBody SquadAcceptRequest squadAcceptRequest,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadParticipantCommandService.acceptRequest(
                authMemberAttribute.id(), crewId, squadId, squadAcceptRequest.toDto()
        );

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/crews/{crewId}/squads/{squadId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> rejectRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long requestId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadParticipantCommandService.rejectRequest(authMemberAttribute.id(), crewId, squadId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<List<SimpleSquadParticipantResponse>>> fetchAllRequests(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SimpleSquadParticipantResponse> simpleSquadParticipantResponses = squadParticipantQueryService
                .fetchAllRequests(authMemberAttribute.id(), crewId, squadId, pageable).stream()
                .map(SimpleSquadParticipantResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(simpleSquadParticipantResponses));
    }

    @DeleteMapping("/crews/{crewId}/squads/{squadId}/requests/me")
    public ResponseEntity<RestResponse<List<SquadParticipantRequestResponse>>> cancelMyRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadParticipantCommandService.cancelMyRequest(authMemberAttribute.id(), crewId, squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/squad-requests/me") // TODO Presentation, Application, Persistence 테스트 보류. 페이지가 나뉠 가능성이 매우 큼
    public ResponseEntity<RestResponse<List<SquadParticipantRequestResponse>>> fetchMyAllRequests(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SquadParticipantRequestResponse> requestResponses = squadParticipantQueryService
                .fetchAllMyRequests(authMemberAttribute.id()).stream()
                .map(SquadParticipantRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }
}
