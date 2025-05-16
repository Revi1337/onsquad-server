package revi1337.onsquad.crew_member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.presentation.dto.response.CrewMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    @GetMapping("/crews/{crewId}/manage/members")
    public ResponseEntity<RestResponse<PageResponse<CrewMemberResponse>>> fetchCrewMembers(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        Page<CrewMemberResponse> pageResponse = crewMemberService
                .fetchCrewMembers(authMemberAttribute.id(), crewId, pageable)
                .map(CrewMemberResponse::from);

        return ResponseEntity.ok().body(RestResponse.success(PageResponse.from(pageResponse)));
    }
}
