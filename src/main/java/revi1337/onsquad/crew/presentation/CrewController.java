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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewService;
import revi1337.onsquad.crew.presentation.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewUpdateRequest;
import revi1337.onsquad.crew.presentation.dto.response.CrewInfoResponse;
import revi1337.onsquad.crew.presentation.dto.response.DuplicateCrewNameResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewController {

    private final CrewService crewService;

    @GetMapping("/check")
    public ResponseEntity<RestResponse<DuplicateCrewNameResponse>> checkCrewNameDuplicate(
            @RequestParam String name,
            @Authenticate AuthMemberAttribute ignored
    ) {
        if (crewService.isDuplicateCrewName(name)) {
            return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(true)));
        }

        return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(false)));
    }

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> newCrew(
            @Valid @RequestPart CrewCreateRequest crewCreateRequest,
            @RequestPart(required = false) MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewService.createNewCrew(authMemberAttribute.id(), crewCreateRequest.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<RestResponse<CrewInfoResponse>> findCrew(
            @PathVariable Long crewId,
            @Authenticate(required = false) AuthMemberAttribute authMemberAttribute
    ) {
        final CrewInfoResponse crewResponse;
        if (authMemberAttribute == null) {
            crewResponse = CrewInfoResponse.from(crewService.findCrewById(crewId));
            return ResponseEntity.ok().body(RestResponse.success(crewResponse));
        }

        crewResponse = CrewInfoResponse.from(crewService.findCrewById(authMemberAttribute.id(), crewId));
        return ResponseEntity.ok().body(RestResponse.success(crewResponse));
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<CrewInfoResponse>>> findCrewsByName(
            @RequestParam(required = false) String crewName,
            @PageableDefault Pageable pageable
    ) {
        List<CrewInfoResponse> crewResponses = crewService.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(crewResponses));
    }

    @PutMapping(value = "/{crewId}", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> updateCrew(
            @PathVariable Long crewId,
            @Valid @RequestPart CrewUpdateRequest crewUpdateRequest,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewService.updateCrew(authMemberAttribute.id(), crewId, crewUpdateRequest.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
