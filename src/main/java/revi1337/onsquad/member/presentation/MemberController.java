package revi1337.onsquad.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberService;
import revi1337.onsquad.member.presentation.dto.request.MemberUpdateRequest;
import revi1337.onsquad.member.presentation.dto.response.MemberInfoResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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

    @PutMapping(consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<MemberInfoResponse>> updateMember(
            @Valid @RequestPart(name = "request") MemberUpdateRequest request,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        memberService.updateProfile(authenticatedMember.toDto().getId(), request.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
