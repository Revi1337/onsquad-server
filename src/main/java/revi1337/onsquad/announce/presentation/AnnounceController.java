package revi1337.onsquad.announce.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.announce.application.AnnounceService;
import revi1337.onsquad.announce.presentation.dto.request.AnnounceCreateRequest;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceInfoResponse;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceInfosWithAuthResponse;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class AnnounceController {

    private final AnnounceService announceService;

    @PostMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<String>> createNewAnnounce(
            @PathVariable Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember,
            @Valid @RequestBody AnnounceCreateRequest createRequest
    ) {
        announceService.createNewAnnounce(authenticatedMember.toDto().getId(), crewId, createRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PatchMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<String>> fixAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        announceService.fixAnnounce(authenticatedMember.toDto().getId(), crewId, announceId);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<AnnounceInfoResponse>> findAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        AnnounceInfoResponse announceInfoResponse = AnnounceInfoResponse.from(
                announceService.findAnnounce(authenticatedMember.toDto().getId(), crewId, announceId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfoResponse));
    }

    @GetMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<List<AnnounceInfoResponse>>> findAnnounces(
            @PathVariable Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<AnnounceInfoResponse> announceInfoResponses = announceService.findAnnounces(
                        authenticatedMember.toDto().getId(), crewId).stream()
                .map(AnnounceInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(announceInfoResponses));
    }

    @GetMapping("/{crewId}/announces/more")
    public ResponseEntity<RestResponse<AnnounceInfosWithAuthResponse>> findMoreAnnounces(
            @PathVariable Long crewId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        AnnounceInfosWithAuthResponse announceInfosWithAuthResponse = AnnounceInfosWithAuthResponse.from(
                announceService.findMoreAnnounces(authenticatedMember.toDto().getId(), crewId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfosWithAuthResponse));
    }
}
