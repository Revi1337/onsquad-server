package revi1337.onsquad.crew.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewConfigService;
import revi1337.onsquad.crew.presentation.dto.request.CrewAcceptRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewUpdateRequest;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/config")
@RestController
public class CrewConfigController {

    private final CrewConfigService crewConfigService;

    @PutMapping(value = "/crew", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> updateCrew(
            @RequestParam @Positive Long crewId,
            @Valid @RequestPart CrewUpdateRequest crewUpdateRequest,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewConfigService.updateCrew(authenticatedMember.toDto().getId(), crewId, crewUpdateRequest.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping("/crew/accept")
    public ResponseEntity<RestResponse<String>> acceptCrewMember(
            @Valid @RequestBody CrewAcceptRequest crewAcceptRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewConfigService.acceptCrewMember(authenticatedMember.toDto().getId(), crewAcceptRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
