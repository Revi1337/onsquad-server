package revi1337.onsquad.announce.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.announce.application.AnnounceCommandService;
import revi1337.onsquad.announce.application.AnnounceQueryService;
import revi1337.onsquad.announce.presentation.dto.request.AnnounceCreateRequest;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceWithStateResponse;
import revi1337.onsquad.announce.presentation.dto.response.AnnouncesWithCreateStateResponse;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class AnnounceController {

    private final AnnounceCommandService announceCommandService;
    private final AnnounceQueryService announceQueryService;

    @PostMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<String>> newAnnounce(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute,
            @Valid @RequestBody AnnounceCreateRequest createRequest
    ) {
        announceCommandService.newAnnounce(authMemberAttribute.id(), crewId, createRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<AnnounceWithStateResponse>> findAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        AnnounceWithStateResponse announceInfoResponse = AnnounceWithStateResponse.from(
                announceQueryService.findAnnounce(authMemberAttribute.id(), crewId, announceId)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceInfoResponse));
    }

    @GetMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<AnnouncesWithCreateStateResponse>> findAnnounces(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        AnnouncesWithCreateStateResponse announceResponses = AnnouncesWithCreateStateResponse.from(
                announceQueryService.findAnnounces(authMemberAttribute.id(), crewId, pageable)
        );

        return ResponseEntity.ok().body(RestResponse.success(announceResponses));
    }

    @PatchMapping("/{crewId}/announces/{announceId}/fix")
    public ResponseEntity<RestResponse<String>> updateAnnounceFixed(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @RequestParam boolean state,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        if (state) {
            announceCommandService.fixAnnounce(authMemberAttribute.id(), crewId, announceId);
        } else {
            announceCommandService.unfixAnnounce(authMemberAttribute.id(), crewId, announceId);
        }

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
