package revi1337.onsquad.crew.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewCommandExecutor;
import revi1337.onsquad.crew.application.CrewQueryService;
import revi1337.onsquad.crew.presentation.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewUpdateRequest;
import revi1337.onsquad.crew.presentation.dto.response.CrewInfoResponse;
import revi1337.onsquad.crew.presentation.dto.response.DuplicateCrewNameResponse;
import revi1337.onsquad.crew_member.presentation.dto.response.EnrolledCrewResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewController {

    private final CrewCommandExecutor crewCommandExecutor;
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

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> newCrew(
            @Valid @RequestPart CrewCreateRequest request,
            @RequestPart(required = false) MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewCommandExecutor.newCrew(authMemberAttribute.id(), request.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.created());
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

    @PutMapping("/{crewId}")
    public ResponseEntity<RestResponse<String>> updateCrew(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewUpdateRequest crewUpdateRequest,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewCommandExecutor.updateCrew(authMemberAttribute.id(), crewId, crewUpdateRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/{crewId}")
    public ResponseEntity<RestResponse<String>> deleteCrew(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewCommandExecutor.deleteCrew(authMemberAttribute.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping(value = "/{crewId}/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<String>> updateCrewImage(
            @PathVariable Long crewId,
            @RequestPart MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewCommandExecutor.updateCrewImage(authMemberAttribute.id(), crewId, file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/{crewId}/image")
    public ResponseEntity<RestResponse<String>> deleteCrewImage(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewCommandExecutor.deleteCrewImage(authMemberAttribute.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
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

    @GetMapping("/me")
    public ResponseEntity<RestResponse<List<EnrolledCrewResponse>>> fetchAllJoinedCrews(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<EnrolledCrewResponse> ownedCrewResponses = crewQueryService
                .fetchAllJoinedCrews(authMemberAttribute.id()).stream()
                .map(EnrolledCrewResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(ownedCrewResponses));
    }
}
