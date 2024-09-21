package revi1337.onsquad.crew.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewService;
import revi1337.onsquad.crew.presentation.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewJoinRequest;
import revi1337.onsquad.crew.presentation.dto.response.CrewInfoResponse;
import revi1337.onsquad.crew.presentation.dto.response.DuplicateCrewNameResponse;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CrewController {

    private final CrewService crewService;

    @GetMapping("/crew/check")
    public ResponseEntity<RestResponse<DuplicateCrewNameResponse>> checkCrewNameDuplicate(
            @RequestParam String crewName
    ) {
        if (crewService.checkDuplicateNickname(crewName)) {
            return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(true)));
        }

        return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(false)));
    }

    @PostMapping( value = "/crew/new", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> createNewCrew(
            @Valid @RequestPart CrewCreateRequest crewCreateRequest,
            @RequestPart MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) throws IOException {
        crewService.createNewCrew(
                authenticatedMember.toDto().getId(), crewCreateRequest.toDto(), file.getBytes(), file.getOriginalFilename()
        );

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/crew")
    public ResponseEntity<RestResponse<CrewInfoResponse>> findCrew(
            @RequestParam String crewName
    ) {
        CrewInfoResponse crewResponse = CrewInfoResponse.from(crewService.findCrewByName(crewName));

        return ResponseEntity.ok().body(RestResponse.success(crewResponse));
    }

    @GetMapping("/crews")
    public ResponseEntity<RestResponse<List<CrewInfoResponse>>> findCrews(
            @RequestParam(required = false) String crewName,
            @PageableDefault Pageable pageable
    ) {
        List<CrewInfoResponse> crewResponses = crewService.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoResponse::from).toList();

        return ResponseEntity.ok().body(RestResponse.success(crewResponses));
    }

    @PostMapping("/crew/join")
    public ResponseEntity<RestResponse<String>> joinCrew(
            @Valid @RequestBody CrewJoinRequest crewJoinRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewService.joinCrew(authenticatedMember.toDto().getId(), crewJoinRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }
}
