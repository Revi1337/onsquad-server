package revi1337.onsquad.member.presentation;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import revi1337.onsquad.member.application.MemberCommandService;
import revi1337.onsquad.member.presentation.dto.request.MemberJoinRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberPasswordUpdateRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberCommandController {

    private final MemberCommandService memberCommandService;

    @PostMapping
    public ResponseEntity<RestResponse<String>> newMember(
            @Valid @RequestBody MemberJoinRequest memberJoinRequest
    ) {
        memberCommandService.newMember(memberJoinRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PutMapping("/my")
    public ResponseEntity<RestResponse<String>> updateMember(
            @Valid @RequestPart(name = "request") MemberUpdateRequest request,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        memberCommandService.updateMember(authMemberAttribute.id(), request.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping("/my/password")
    public ResponseEntity<RestResponse<String>> updatePassword(
            @Valid @RequestBody MemberPasswordUpdateRequest request,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        memberCommandService.updatePassword(authMemberAttribute.id(), request.toDto());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @PatchMapping(value = "/my/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<String>> updateMemberImage(
            @RequestPart MultipartFile file,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        memberCommandService.updateMemberImage(authMemberAttribute.id(), file);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/my/image")
    public ResponseEntity<RestResponse<String>> deleteMemberImage(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        memberCommandService.deleteMemberImage(authMemberAttribute.id());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
