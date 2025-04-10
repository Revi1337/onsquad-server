package revi1337.onsquad.crew.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewCommandExecutor;
import revi1337.onsquad.crew.presentation.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class CrewCommandController {

    private final CrewCommandExecutor crewCommandExecutor;

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> newCrew(
            @Valid @RequestPart CrewCreateRequest crewCreateRequest,
            @RequestPart(required = false) MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        crewCommandExecutor.newCrew(authMemberAttribute.id(), crewCreateRequest.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.created());
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
}
