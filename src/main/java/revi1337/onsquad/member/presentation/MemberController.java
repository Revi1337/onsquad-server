package revi1337.onsquad.member.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import revi1337.onsquad.member.application.MemberService;
import revi1337.onsquad.member.presentation.dto.request.MemberJoinRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberPasswordUpdateRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberUpdateRequest;
import revi1337.onsquad.member.presentation.dto.response.DuplicateResponse;
import revi1337.onsquad.member.presentation.dto.response.MemberInfoResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/verify/nickname/{nickname}")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkDuplicateNickname(
            @PathVariable String nickname
    ) {
        if (memberService.checkDuplicateNickname(nickname)) {
            return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(false)));
    }

    @GetMapping("/members/verify/email/{email}")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkDuplicateEmail(
            @PathVariable String email
    ) {
        if (memberService.checkDuplicateEmail(email)) {
            return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(false)));
    }

    @PostMapping("/members")
    public ResponseEntity<RestResponse<String>> newMember(
            @Valid @RequestBody MemberJoinRequest memberJoinRequest
    ) {
        memberService.newMember(memberJoinRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/my")
    public ResponseEntity<RestResponse<MemberInfoResponse>> findMember(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        MemberInfoResponse memberInfoResponse = MemberInfoResponse.from(
                memberService.findMember(authMemberAttribute.id())
        );

        return ResponseEntity.ok().body(RestResponse.success(memberInfoResponse));
    }

    @PatchMapping("/my")
    public ResponseEntity<RestResponse<String>> updatePassword(
            @Valid @RequestBody MemberPasswordUpdateRequest request,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        memberService.updatePassword(authMemberAttribute.id(), request.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PutMapping(value = "/my", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<RestResponse<String>> updateMember(
            @Valid @RequestPart(name = "request") MemberUpdateRequest request,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        memberService.updateProfile(authMemberAttribute.id(), request.toDto(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
