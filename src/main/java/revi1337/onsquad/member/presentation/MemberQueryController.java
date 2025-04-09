package revi1337.onsquad.member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberQueryService;
import revi1337.onsquad.member.presentation.dto.response.DuplicateResponse;
import revi1337.onsquad.member.presentation.dto.response.MemberInfoResponse;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/verify/nickname/{nickname}")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkDuplicateNickname(
            @PathVariable String nickname
    ) {
        if (memberQueryService.checkDuplicateNickname(nickname)) {
            return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(false)));
    }

    @GetMapping("/verify/email/{email}")
    public ResponseEntity<RestResponse<DuplicateResponse>> checkDuplicateEmail(
            @PathVariable String email
    ) {
        if (memberQueryService.checkDuplicateEmail(email)) {
            return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(DuplicateResponse.of(false)));
    }

    @GetMapping("/my")
    public ResponseEntity<RestResponse<MemberInfoResponse>> findMember(
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        MemberInfoResponse memberInfoResponse = MemberInfoResponse.from(
                memberQueryService.findMember(authMemberAttribute.id())
        );

        return ResponseEntity.ok().body(RestResponse.success(memberInfoResponse));
    }
}
