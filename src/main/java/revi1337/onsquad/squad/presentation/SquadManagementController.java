package revi1337.onsquad.squad.presentation;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadManagementService;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoWithOwnerFlagResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadManagementController {

    private final SquadManagementService squadManagementService;

    @GetMapping("/manage/squads")
    public ResponseEntity<RestResponse<List<SimpleSquadInfoWithOwnerFlagResponse>>> fetchSquadsWithOwnerFlag(
            @Authenticate AuthenticatedMember authenticatedMember,
            @RequestParam @Positive Long crewId,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        List<SimpleSquadInfoWithOwnerFlagResponse> simpleSquadInfoWithOwnerFlagResponses = squadManagementService
                .fetchSquadsWithOwnerFlag(authenticatedMember.toDto().getId(), crewId, pageable).stream()
                .map(SimpleSquadInfoWithOwnerFlagResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(simpleSquadInfoWithOwnerFlagResponses));
    }
}
