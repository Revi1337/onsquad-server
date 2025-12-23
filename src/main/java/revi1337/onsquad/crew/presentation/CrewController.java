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
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewCommandService;
import revi1337.onsquad.crew.application.CrewCreationCoordinator;
import revi1337.onsquad.crew.application.CrewImageUpdateCoordinator;
import revi1337.onsquad.crew.application.CrewQueryService;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.application.dto.response.DuplicateCrewNameResponse;
import revi1337.onsquad.crew.presentation.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.request.CrewUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewController {

    private final CrewCreationCoordinator crewCreationCoordinator;
    private final CrewImageUpdateCoordinator crewImageUpdateCoordinator;
    private final CrewCommandService crewCommandService;
    private final CrewQueryService crewQueryService;

    @GetMapping("/check")
    public ResponseEntity<RestResponse<DuplicateCrewNameResponse>> checkNameDuplicate(
            @RequestParam String name,
            @Authenticate CurrentMember ignored
    ) {
        DuplicateCrewNameResponse response = crewQueryService.checkNameDuplicate(name);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<Void>> newCrew(
            @Valid @RequestPart CrewCreateRequest request,
            @RequestPart(required = false) MultipartFile file,
            @Authenticate CurrentMember currentMember
    ) {
        crewCreationCoordinator.newCrew(currentMember.id(), request.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<RestResponse<CrewWithParticipantStateResponse>> findCrew(
            @PathVariable Long crewId,
            @Authenticate(required = false) CurrentMember currentMember
    ) {
        Long memberId = currentMember == null ? null : currentMember.id();
        CrewWithParticipantStateResponse response = crewQueryService.findCrewById(memberId, crewId);

        return ResponseEntity.ok(RestResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<CrewResponse>>> fetchCrewsByName(
            @RequestParam(required = false) String name,
            @PageableDefault Pageable pageable
    ) {
        List<CrewResponse> response = crewQueryService.fetchCrewsByName(name, pageable);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @PutMapping("/{crewId}")
    public ResponseEntity<RestResponse<Void>> updateCrew(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewUpdateRequest crewUpdateRequest,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandService.updateCrew(currentMember.id(), crewId, crewUpdateRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/{crewId}")
    public ResponseEntity<RestResponse<Void>> deleteCrew(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandService.deleteCrew(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping(value = "/{crewId}/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<Void>> updateCrewImage(
            @PathVariable Long crewId,
            @RequestPart MultipartFile file,
            @Authenticate CurrentMember currentMember
    ) {
        crewImageUpdateCoordinator.updateImage(currentMember.id(), crewId, file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/{crewId}/image")
    public ResponseEntity<RestResponse<Void>> deleteCrewImage(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandService.deleteImage(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
