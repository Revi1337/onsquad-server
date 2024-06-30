package revi1337.onsquad.member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberService;
import revi1337.onsquad.member.dto.response.MemberInfoResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<RestResponse<MemberInfoResponse>> findMember(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        MemberInfoResponse memberInfoResponse = MemberInfoResponse
                .from(memberService.findMember(authenticatedMember.toDto().getId()));

        return ResponseEntity.ok().body(RestResponse.success(memberInfoResponse));
    }
}
