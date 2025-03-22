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
import revi1337.onsquad.auth.application.AuthMemberAttribute;
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
            @Authenticate AuthMemberAttribute authMemberAttribute,
            @Valid @RequestBody AnnounceCreateRequest createRequest
    ) {
        announceService.createNewAnnounce(authMemberAttribute.id(), crewId, createRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PatchMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<String>> fixAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        announceService.fixAnnounce(authMemberAttribute.id(), crewId, announceId);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<AnnounceInfoResponse>> findAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        AnnounceInfoResponse announceInfoResponse = AnnounceInfoResponse.from(
                announceService.findAnnounce(authMemberAttribute.id(), crewId, announceId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfoResponse));
    }

    @GetMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<List<AnnounceInfoResponse>>> findAnnounces(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<AnnounceInfoResponse> announceInfoResponses = announceService
                .findAnnounces(authMemberAttribute.id(), crewId).stream()
                .map(AnnounceInfoResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(announceInfoResponses));
    }

    @GetMapping("/{crewId}/announces/more")
    public ResponseEntity<RestResponse<AnnounceInfosWithAuthResponse>> findMoreAnnounces(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        AnnounceInfosWithAuthResponse announceInfosWithAuthResponse = AnnounceInfosWithAuthResponse.from(
                announceService.findMoreAnnounces(authMemberAttribute.id(), crewId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfosWithAuthResponse));
    }
}
