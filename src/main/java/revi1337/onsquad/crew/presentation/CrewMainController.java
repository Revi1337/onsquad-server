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
import revi1337.onsquad.crew.application.dto.response.CrewManageResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CrewMainController {

    private final CrewMainService crewMainService;

    @GetMapping("/crews/{crewId}/main")
    public ResponseEntity<RestResponse<CrewMainResponse>> fetchMain(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        CrewMainResponse response = crewMainService.fetchMain(currentMember.id(), crewId, pageable);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @GetMapping("/crews/{crewId}/manage")
    public ResponseEntity<RestResponse<CrewManageResponse>> fetchManageInfo(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        CrewManageResponse response = crewMainService.fetchManageInfo(currentMember.id(), crewId);

        return ResponseEntity.ok(RestResponse.success(response));
    }
}
