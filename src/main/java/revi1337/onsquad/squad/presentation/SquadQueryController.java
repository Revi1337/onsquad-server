package revi1337.onsquad.squad.presentation;

import java.util.List;
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
import revi1337.onsquad.squad.application.SquadQueryService;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoWithOwnerFlagResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadInfoResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadQueryController {

    private final SquadQueryService squadQueryService;

    @GetMapping("/{crewId}/squads/{squadId}")
    public ResponseEntity<RestResponse<SquadInfoResponse>> findSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        SquadInfoResponse squadResponse = SquadInfoResponse.from(
                squadQueryService.findSquad(authMemberAttribute.id(), crewId, squadId)
        );

        return ResponseEntity.ok(RestResponse.success(squadResponse));
    }

    @GetMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<List<SquadInfoResponse>>> findSquads(
            @PathVariable Long crewId,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable
    ) {
        List<SquadInfoResponse> squadResponses = squadQueryService.findSquads(crewId, category, pageable).stream()
                .map(SquadInfoResponse::from)
                .toList();

        return ResponseEntity.ok(RestResponse.success(squadResponses));
    }

    @GetMapping("/{crewId}/squads/manage")
    public ResponseEntity<RestResponse<List<SimpleSquadInfoWithOwnerFlagResponse>>> fetchSquadsWithOwnerFlag(
            @PathVariable Long crewId,
            @PageableDefault(size = 5) Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SimpleSquadInfoWithOwnerFlagResponse> simpleSquadInfoWithOwnerFlagResponses = squadQueryService
                .fetchSquadsWithOwnerFlag(authMemberAttribute.id(), crewId, pageable).stream()
                .map(SimpleSquadInfoWithOwnerFlagResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(simpleSquadInfoWithOwnerFlagResponses));
    }
}
