package revi1337.onsquad.squad.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadManagementService;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadManagementController {

    private final SquadManagementService squadManagementService;

    @GetMapping("/manage/squad")
    public ResponseEntity<RestResponse<List<SimpleSquadInfoResponse>>> findSquadsInCrew(
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SimpleSquadInfoResponse> squadInfoResponses = squadManagementService.findSquadsInCrew(authenticatedMember.toDto().getId(), crewName).stream()
                .map(SimpleSquadInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(squadInfoResponses));
    }
}
