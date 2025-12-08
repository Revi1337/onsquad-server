package revi1337.onsquad.crew_request.presentation;

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
import revi1337.onsquad.crew_request.application.CrewRequestCommandService;
import revi1337.onsquad.crew_request.application.CrewRequestQueryService;
import revi1337.onsquad.crew_request.presentation.dto.response.CrewRequestWithCrewResponse;
import revi1337.onsquad.crew_request.presentation.dto.response.CrewRequestWithMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewRequestController {

    private final CrewRequestCommandService crewRequestCommandService;
    private final CrewRequestQueryService crewRequestQueryService;

    @PostMapping("/crews/{crewId}/requests")
    public ResponseEntity<RestResponse<String>> request(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        crewRequestCommandService.request(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PatchMapping("/crews/{crewId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> acceptRequest(
            @PathVariable Long crewId,
            @PathVariable Long requestId,
            @Authenticate CurrentMember currentMember
    ) {
        crewRequestCommandService.acceptRequest(currentMember.id(), crewId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/crews/{crewId}/requests/{requestId}")
    public ResponseEntity<RestResponse<String>> rejectRequest(
            @PathVariable Long crewId,
            @PathVariable Long requestId,
            @Authenticate CurrentMember currentMember
    ) {
        crewRequestCommandService.rejectRequest(currentMember.id(), crewId, requestId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crews/{crewId}/manage/requests")
    public ResponseEntity<RestResponse<List<CrewRequestWithMemberResponse>>> fetchAllRequests(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        List<CrewRequestWithMemberResponse> requestResponses = crewRequestQueryService
                .fetchAllRequests(currentMember.id(), crewId, pageable).stream()
                .map(CrewRequestWithMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(requestResponses));
    }

    @DeleteMapping("/crews/{crewId}/requests/me")
    public ResponseEntity<RestResponse<String>> cancelMyRequest(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        crewRequestCommandService.cancelMyRequest(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/crew-requests/me")
    public ResponseEntity<RestResponse<List<CrewRequestWithCrewResponse>>> fetchAllCrewRequests(
            @Authenticate CurrentMember currentMember
    ) {
        List<CrewRequestWithCrewResponse> crewRequestWithCrewResponse = crewRequestQueryService
                .fetchAllCrewRequests(currentMember.id()).stream()
                .map(CrewRequestWithCrewResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewRequestWithCrewResponse));
    }
}
