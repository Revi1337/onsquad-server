package revi1337.onsquad.crew.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewConfigService;
import revi1337.onsquad.crew.presentation.dto.request.CrewAcceptRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewUpdateRequest;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequiredArgsConstructor
@RequestMapping("/api/v1/config")
@RestController
public class CrewConfigController {

    private final CrewConfigService crewConfigService;

    @PutMapping(value = "/crew", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> updateCrew(
            @RequestParam String crewName,
            @Valid @RequestPart CrewUpdateRequest crewUpdateRequest,
            @RequestPart MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) throws IOException {
        crewConfigService.updateCrew(
                authenticatedMember.toDto().getId(), crewName, crewUpdateRequest.toDto(), file.getBytes(), file.getOriginalFilename()
        );

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
