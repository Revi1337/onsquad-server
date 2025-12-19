package revi1337.onsquad.crew.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewMainService;
import revi1337.onsquad.crew.application.dto.response.CrewMainResponse;
import revi1337.onsquad.crew.application.dto.response.CrewStatisticResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewMainController {

    private final CrewMainService crewMainService;

    @GetMapping("/{crewId}/main")
    public ResponseEntity<RestResponse<CrewMainResponse>> fetchMain(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        CrewMainResponse response = crewMainService.fetchMain(currentMember.id(), crewId, pageable);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @GetMapping("/{crewId}/manage")
    public ResponseEntity<RestResponse<CrewStatisticResponse>> fetchCrewStatistic(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        CrewStatisticResponse response = crewMainService.calculateStatistic(currentMember.id(), crewId);

        return ResponseEntity.ok(RestResponse.success(response));
    }
}
