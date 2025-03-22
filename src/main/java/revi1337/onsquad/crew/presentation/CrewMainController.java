package revi1337.onsquad.crew.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewMainService;
import revi1337.onsquad.crew.presentation.dto.response.CrewMainResponse;
import revi1337.onsquad.crew.presentation.dto.response.CrewStatisticResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewMainController {

    private final CrewMainService crewMainService;

    // TODO @RequestParam CategoryCondition category 이거 왜 필수값이지? 고민필요.
    @GetMapping("/{crewId}/main")
    public ResponseEntity<RestResponse<CrewMainResponse>> fetchMain(
            @PathVariable Long crewId,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        CrewMainResponse crewMainResponse = CrewMainResponse.from(
                crewMainService.fetchMain(authMemberAttribute.id(), crewId, category.categoryType(), pageable)
        );

        return ResponseEntity.ok().body(RestResponse.success(crewMainResponse));
    }

    @GetMapping("/{crewId}/statistic")
    public ResponseEntity<RestResponse<CrewStatisticResponse>> fetchCrewStatistic(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        CrewStatisticResponse crewStatisticResponse = CrewStatisticResponse.from(
                crewMainService.calculateStatistic(authMemberAttribute.id(), crewId)
        );

        return ResponseEntity.ok(RestResponse.success(crewStatisticResponse));
    }
}
