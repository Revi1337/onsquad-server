package revi1337.onsquad.member.presentation;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberCommandService;
import revi1337.onsquad.member.application.MemberImageUpdateCoordinator;
import revi1337.onsquad.member.application.MemberQueryService;
import revi1337.onsquad.member.application.dto.response.DuplicateResponse;
import revi1337.onsquad.member.application.dto.response.MemberResponse;
import revi1337.onsquad.member.presentation.request.MemberCreateRequest;
import revi1337.onsquad.member.presentation.request.MemberPasswordUpdateRequest;
import revi1337.onsquad.member.presentation.request.MemberUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberImageUpdateCoordinator imageUpdateCoordinator;
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @GetMapping("/check-nickname")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkDuplicateNickname(
            @RequestParam String value
    ) {
        DuplicateResponse response = memberQueryService.checkDuplicateNickname(value);

        return ResponseEntity.ok(RestResponse.success(response));
    }

    @GetMapping("/check-email")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkDuplicateEmail(
            @RequestParam String value
    ) {
        DuplicateResponse response = memberQueryService.checkDuplicateEmail(value);

        return ResponseEntity.ok(RestResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<RestResponse<Void>> newMember(
            @Valid @RequestBody MemberCreateRequest memberCreateRequest
    ) {
        memberCommandService.newMember(memberCreateRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponse<MemberResponse>> findMember(
            @Authenticate CurrentMember currentMember
    ) {
        MemberResponse response = memberQueryService.findMember(currentMember.id());

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @PutMapping("/me")
    public ResponseEntity<RestResponse<Void>> updateMember(
            @Valid @RequestBody MemberUpdateRequest request,
            @Authenticate CurrentMember currentMember
    ) {
        memberCommandService.updateMember(currentMember.id(), request.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping("/me/password")
    public ResponseEntity<RestResponse<Void>> updatePassword(
            @Valid @RequestBody MemberPasswordUpdateRequest request,
            @Authenticate CurrentMember currentMember
    ) {
        memberCommandService.updatePassword(currentMember.id(), request.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping(value = "/me/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<Void>> updateImage(
            @RequestPart MultipartFile file,
            @Authenticate CurrentMember currentMember
    ) {
        imageUpdateCoordinator.updateImage(currentMember.id(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/me/image")
    public ResponseEntity<RestResponse<Void>> deleteImage(
            @Authenticate CurrentMember currentMember
    ) {
        memberCommandService.deleteImage(currentMember.id());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
