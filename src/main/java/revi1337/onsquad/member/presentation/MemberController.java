package revi1337.onsquad.member.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberService;
import revi1337.onsquad.member.presentation.dto.request.MemberPasswordUpdateRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberUpdateRequest;
import revi1337.onsquad.member.presentation.dto.response.MemberInfoResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/my")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<RestResponse<MemberInfoResponse>> findMember(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        MemberInfoResponse memberInfoResponse = MemberInfoResponse
                .from(memberService.findMember(authenticatedMember.toDto().getId()));

        return ResponseEntity.ok().body(RestResponse.success(memberInfoResponse));
    }

    @PutMapping
    public ResponseEntity<RestResponse<String>> updatePassword(
            @Valid @RequestBody MemberPasswordUpdateRequest request,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        memberService.updatePassword(authenticatedMember.toDto().getId(), request.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> updateMember(
            @Valid @RequestPart(name = "request") MemberUpdateRequest request,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        memberService.updateProfile(authenticatedMember.toDto().getId(), request.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
