package revi1337.onsquad.crew.presentation;

import jakarta.validation.constraints.Positive;
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
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewMainService;
import revi1337.onsquad.crew.presentation.dto.response.CrewMainResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewMainController {

    private final CrewMainService crewMainService;

    // TODO @RequestParam CategoryCondition category 이거 왜 필수값이지? 고민필요.
    @GetMapping("/main")
    public ResponseEntity<RestResponse<CrewMainResponse>> fetchMain(
            @RequestParam @Positive Long crewId,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CrewMainResponse crewMainResponse = CrewMainResponse.from(
                crewMainService.fetchMain(authenticatedMember.toDto().getId(), crewId, category.categoryType(),
                        pageable)
        );

        return ResponseEntity.ok().body(RestResponse.success(crewMainResponse));
    }
}
