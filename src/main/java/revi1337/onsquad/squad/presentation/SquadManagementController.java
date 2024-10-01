package revi1337.onsquad.squad.presentation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadManagementService;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoResponse;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadManagementController {

    private final SquadManagementService squadManagementService;

    @GetMapping("/manage/squads")
    public ResponseEntity<RestResponse<List<SimpleSquadInfoResponse>>> findSquadsInCrew(
            @RequestParam @Positive Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SimpleSquadInfoResponse> squadInfoResponses = squadManagementService.findSquadsInCrew(authenticatedMember.toDto().getId(), crewId).stream()
                .map(SimpleSquadInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(squadInfoResponses));
    }
}
