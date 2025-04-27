package revi1337.onsquad.crew.presentation;

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
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewQueryService;
import revi1337.onsquad.crew.presentation.dto.response.CrewInfoResponse;
import revi1337.onsquad.crew.presentation.dto.response.DuplicateCrewNameResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewQueryController {

    private final CrewQueryService crewQueryService;

    @GetMapping("/check")
    public ResponseEntity<RestResponse<DuplicateCrewNameResponse>> checkCrewNameDuplicate(
            @RequestParam String name,
            @Authenticate AuthMemberAttribute ignored
    ) {
        if (crewQueryService.isDuplicateCrewName(name)) {
            return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(true)));
        }

        return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(false)));
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<RestResponse<CrewInfoResponse>> findCrew(
            @PathVariable Long crewId,
            @Authenticate(required = false) AuthMemberAttribute authMemberAttribute
    ) {
        final CrewInfoResponse crewResponse;
        if (authMemberAttribute == null) {
            crewResponse = CrewInfoResponse.from(crewQueryService.findCrewById(crewId));
            return ResponseEntity.ok().body(RestResponse.success(crewResponse));
        }

        crewResponse = CrewInfoResponse.from(crewQueryService.findCrewById(authMemberAttribute.id(), crewId));
        return ResponseEntity.ok().body(RestResponse.success(crewResponse));
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<CrewInfoResponse>>> fetchCrewsByName(
            @RequestParam(required = false) String name,
            @PageableDefault Pageable pageable
    ) {
        List<CrewInfoResponse> crewResponses = crewQueryService.fetchCrewsByName(name, pageable).stream()
                .map(CrewInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewResponses));
    }
}
