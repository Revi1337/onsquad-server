package revi1337.onsquad.announce.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.announce.application.AnnounceCommandService;
import revi1337.onsquad.announce.application.AnnounceQueryService;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithPinAndModifyStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnouncesWithWriteStateResponse;
import revi1337.onsquad.announce.presentation.request.AnnounceCreateRequest;
import revi1337.onsquad.announce.presentation.request.AnnounceUpdateRequest;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;

@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class AnnounceController {

    private final AnnounceCommandService announceCommandService;
    private final AnnounceQueryService announceQueryService;

    @PostMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<Void>> newAnnounce(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember,
            @Valid @RequestBody AnnounceCreateRequest createRequest
    ) {
        announceCommandService.newAnnounce(currentMember.id(), crewId, createRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<AnnounceWithPinAndModifyStateResponse>> findAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate CurrentMember currentMember
    ) {
        AnnounceWithPinAndModifyStateResponse response = announceQueryService.findAnnounce(currentMember.id(), crewId, announceId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @GetMapping("/{crewId}/announces")
    public ResponseEntity<RestResponse<AnnouncesWithWriteStateResponse>> findAnnounces(
            @PathVariable Long crewId,
            @Authenticate CurrentMember currentMember
    ) {
        AnnouncesWithWriteStateResponse response = announceQueryService.findAnnounces(currentMember.id(), crewId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @PutMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<Void>> updateAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Valid @RequestBody AnnounceUpdateRequest updateRequest,
            @Authenticate CurrentMember currentMember
    ) {
        announceCommandService.updateAnnounce(currentMember.id(), crewId, announceId, updateRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping("/{crewId}/announces/{announceId}/pin")
    public ResponseEntity<RestResponse<Void>> changeAnnouncePinned(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @RequestParam boolean state,
            @Authenticate CurrentMember currentMember
    ) {
        announceCommandService.changePinState(currentMember.id(), crewId, announceId, state);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/{crewId}/announces/{announceId}")
    public ResponseEntity<RestResponse<Void>> deleteAnnounce(
            @PathVariable Long crewId,
            @PathVariable Long announceId,
            @Authenticate CurrentMember currentMember
    ) {
        announceCommandService.deleteAnnounce(currentMember.id(), crewId, announceId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
