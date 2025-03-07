package revi1337.onsquad.crew.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewManagementService;
import revi1337.onsquad.crew.presentation.dto.response.CrewStatisticResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CrewManagementController {

    private final CrewManagementService crewManagementService;

    @GetMapping("/crews/{crewId}/manage")
    public ResponseEntity<RestResponse<CrewStatisticResponse>> fetchCrewStatistic(
            @Authenticate AuthenticatedMember authenticatedMember,
            @PathVariable Long crewId
    ) {
        CrewStatisticResponse crewStatisticResponse = CrewStatisticResponse.from(
                crewManagementService.calculateStatistic(authenticatedMember.toDto().getId(), crewId)
        );

        return ResponseEntity.ok(RestResponse.success(crewStatisticResponse));
    }
}
