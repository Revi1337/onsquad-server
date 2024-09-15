package revi1337.onsquad.announce.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.announce.application.AnnounceService;
import revi1337.onsquad.announce.presentation.dto.request.AnnounceCreateRequest;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceInfoResponse;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceInfosWithAuthResponse;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class AnnounceController {

    private final AnnounceService announceService;

    @PostMapping("/announce/new")
    public ResponseEntity<RestResponse<String>> createNewAnnounce(
            @Valid @RequestBody AnnounceCreateRequest createRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        announceService.createNewAnnounce(authenticatedMember.toDto().getId(), createRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/announce")
    public ResponseEntity<RestResponse<AnnounceInfoResponse>> findAnnounce(
            @RequestParam Long crewId,
            @RequestParam Long announceId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        AnnounceInfoResponse announceInfoResponse = AnnounceInfoResponse.from(
                announceService.findAnnounce(authenticatedMember.toDto().getId(), crewId, announceId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfoResponse));
    }

    @GetMapping("/announces")
    public ResponseEntity<RestResponse<AnnounceInfosWithAuthResponse>> findAnnounces(
            @RequestParam Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        AnnounceInfosWithAuthResponse announceInfosWithAuthResponse = AnnounceInfosWithAuthResponse.from(
                announceService.findAnnounces(authenticatedMember.toDto().getId(), crewId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfosWithAuthResponse));
    }
}
