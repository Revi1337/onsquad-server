package revi1337.onsquad.squad.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.SquadService;
import revi1337.onsquad.squad.presentation.dto.request.SquadCreateRequest;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoWithOwnerFlagResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadInfoResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadController {

    private final SquadService squadService;

    @PostMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<String>> createNewSquad(
            @PathVariable Long crewId,
            @Valid @RequestBody SquadCreateRequest squadCreateRequest,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadService.createNewSquad(authMemberAttribute.id(), crewId, squadCreateRequest.toDto());

        return ResponseEntity.ok(RestResponse.created());
    }

    @GetMapping("/{crewId}/squads/{squadId}")
    public ResponseEntity<RestResponse<SquadInfoResponse>> findSquad(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        SquadInfoResponse squadResponse = SquadInfoResponse.from(
                squadService.findSquad(authMemberAttribute.id(), crewId, squadId)
        );

        return ResponseEntity.ok(RestResponse.success(squadResponse));
    }

    @GetMapping("/{crewId}/squads")
    public ResponseEntity<RestResponse<List<SquadInfoResponse>>> findSquads(
            @PathVariable Long crewId,
            @RequestParam CategoryCondition category,
            @PageableDefault Pageable pageable
    ) {
        List<SquadInfoResponse> squadResponses = squadService.findSquads(crewId, category, pageable).stream()
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
        List<SimpleSquadInfoWithOwnerFlagResponse> simpleSquadInfoWithOwnerFlagResponses = squadService
                .fetchSquadsWithOwnerFlag(authMemberAttribute.id(), crewId, pageable).stream()
                .map(SimpleSquadInfoWithOwnerFlagResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(simpleSquadInfoWithOwnerFlagResponses));
    }
}
