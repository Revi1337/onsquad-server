package revi1337.onsquad.crew.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
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
import revi1337.onsquad.common.dto.DuplicateResponse;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewCommandServiceFacade;
import revi1337.onsquad.crew.application.CrewQueryService;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.presentation.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.request.CrewUpdateRequest;

@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class CrewController {

    private final CrewCommandServiceFacade crewCommandServiceFacade;
    private final CrewQueryService crewQueryService;

    @GetMapping("/check")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkNameDuplicate(
            @RequestParam String name,
            @Authenticate CurrentMember ignored
    ) {
        DuplicateResponse response = crewQueryService.checkNameDuplicate(name);
        RestResponse<DuplicateResponse> restResponse = RestResponse.success(response);

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<Void>> newCrew(
            @Valid @RequestPart CrewCreateRequest request,
            @RequestPart(required = false) MultipartFile file,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandServiceFacade.newCrew(currentMember.id(), request.toDto(), file);
        RestResponse<Void> restResponse = RestResponse.created();

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<RestResponse<CrewWithParticipantStateResponse>> findCrew(
            @PathVariable Long crewId,
            @Authenticate(required = false) CurrentMember currentMember
    ) {
        Long memberId = currentMember == null ? null : currentMember.id();
        CrewWithParticipantStateResponse response = crewQueryService.findCrewById(memberId, crewId);
        RestResponse<CrewWithParticipantStateResponse> restResponse = RestResponse.success(response);

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @GetMapping
    public ResponseEntity<RestResponse<PageResponse<CrewResponse>>> fetchCrewsByName(
            @RequestParam(required = false) String name,
            @PageableDefault Pageable pageable
    ) {
        PageResponse<CrewResponse> response = crewQueryService.fetchCrewsByName(name, pageable);
        RestResponse<PageResponse<CrewResponse>> restResponse = RestResponse.success(response);

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @PutMapping("/{crewId}")
    public ResponseEntity<RestResponse<Void>> updateCrew(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewUpdateRequest crewUpdateRequest,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandServiceFacade.updateCrew(currentMember.id(), crewId, crewUpdateRequest.toDto());
        RestResponse<Void> restResponse = RestResponse.ok();

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @DeleteMapping("/{crewId}")
    public ResponseEntity<RestResponse<Void>> deleteCrew(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandServiceFacade.deleteCrew(currentMember.id(), crewId);
        RestResponse<Void> restResponse = RestResponse.ok();

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @PatchMapping(value = "/{crewId}/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<Void>> updateCrewImage(
            @PathVariable Long crewId,
            @RequestPart MultipartFile file,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandServiceFacade.updateImage(currentMember.id(), crewId, file);
        RestResponse<Void> restResponse = RestResponse.ok();

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @DeleteMapping("/{crewId}/image")
    public ResponseEntity<RestResponse<Void>> deleteCrewImage(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        crewCommandServiceFacade.deleteImage(currentMember.id(), crewId);
        RestResponse<Void> restResponse = RestResponse.ok();

        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }
}
