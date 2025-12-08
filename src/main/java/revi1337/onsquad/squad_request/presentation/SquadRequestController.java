package revi1337.onsquad.squad_request.presentation;

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
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_request.application.SquadRequestCommandService;
import revi1337.onsquad.squad_request.application.SquadRequestQueryService;
import revi1337.onsquad.squad_request.presentation.dto.SquadRequestResponse;
import revi1337.onsquad.squad_request.presentation.dto.SquadRequestWithSquadAndCrewResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadRequestController {

    private final SquadRequestCommandService squadRequestCommandService;
    private final SquadRequestQueryService squadRequestQueryService;

    @PostMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<String>> request(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        squadRequestCommandService.request(currentMember.id(), crewId, squadId);

        return ResponseEntity.ok(RestResponse.created());
    }

    @PatchMapping("/crews/{crewId}/squads/{squadId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> acceptRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long requestId,
            @Authenticate CurrentMember currentMember
    ) {
        squadRequestCommandService.acceptRequest(currentMember.id(), crewId, squadId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/crews/{crewId}/squads/{squadId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> rejectRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long requestId,
            @Authenticate CurrentMember currentMember
    ) {
        squadRequestCommandService.rejectRequest(currentMember.id(), crewId, squadId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/crews/{crewId}/squads/{squadId}/requests/me")
    public ResponseEntity<RestResponse<String>> cancelMyRequest(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        squadRequestCommandService.cancelMyRequest(currentMember.id(), crewId, squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/requests")
    public ResponseEntity<RestResponse<List<SquadRequestResponse>>> fetchAllRequests(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadRequestResponse> squadRequestRespons = squadRequestQueryService
                .fetchAllRequests(currentMember.id(), crewId, squadId, pageable).stream()
                .map(SquadRequestResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(squadRequestRespons));
    }

    @GetMapping("/squad-requests/me") // TODO Presentation, Application, Persistence 테스트 보류. 페이지가 나뉠 가능성이 매우 큼
    public ResponseEntity<RestResponse<List<SquadRequestWithSquadAndCrewResponse>>> fetchMyAllRequests(
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadRequestWithSquadAndCrewResponse> requestResponses = squadRequestQueryService
                .fetchAllMyRequests(currentMember.id()).stream()
                .map(SquadRequestWithSquadAndCrewResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }
}
