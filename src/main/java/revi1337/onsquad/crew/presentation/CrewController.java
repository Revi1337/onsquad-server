package revi1337.onsquad.crew.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewService;
import revi1337.onsquad.crew.presentation.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.dto.response.CrewInfoResponse;
import revi1337.onsquad.crew.presentation.dto.response.DuplicateCrewNameResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CrewController {

    private final CrewService crewService;

    @GetMapping("/crews/check")
    public ResponseEntity<RestResponse<DuplicateCrewNameResponse>> checkCrewNameDuplicate(
            @RequestParam String name,
            @Authenticate AuthenticatedMember ignored
    ) {
        if (crewService.isDuplicateCrewName(name)) {
            return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(true)));
        }

        return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(false)));
    }

    @PostMapping(value = "/crews", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> createNewCrew(
            @Valid @RequestPart CrewCreateRequest crewCreateRequest,
            @RequestPart(required = false) MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewService.createNewCrew(authenticatedMember.toDto().getId(), crewCreateRequest.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PostMapping("/crews/{crewId}/requests")
    public ResponseEntity<RestResponse<String>> joinCrew(
            @PathVariable Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewService.joinCrew(authenticatedMember.toDto().getId(), crewId);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/crews/{crewId}")
    public ResponseEntity<RestResponse<CrewInfoResponse>> findCrew(
            @PathVariable Long crewId,
            @Authenticate(required = false) AuthenticatedMember authenticatedMember
    ) {
        final CrewInfoResponse crewResponse;
        if (authenticatedMember == null) {
            crewResponse = CrewInfoResponse.from(crewService.findCrewById(crewId));
            return ResponseEntity.ok().body(RestResponse.success(crewResponse));
        }

        crewResponse = CrewInfoResponse.from(crewService.findCrewById(authenticatedMember.toDto().getId(), crewId));
        return ResponseEntity.ok().body(RestResponse.success(crewResponse));
    }

    @GetMapping("/crews")
    public ResponseEntity<RestResponse<List<CrewInfoResponse>>> findCrews(
            @RequestParam(required = false) String crewName,
            @PageableDefault Pageable pageable
    ) {
        List<CrewInfoResponse> crewResponses = crewService.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewResponses));
    }
}
