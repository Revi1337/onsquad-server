package revi1337.onsquad.member.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.dto.request.MemberJoinRequest;
import revi1337.onsquad.member.dto.response.DuplicateNicknameResponse;
import revi1337.onsquad.member.dto.response.EmailValidResponse;

import static org.springframework.http.HttpStatus.*;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class MemberJoinController {

    private final MemberJoinService memberJoinService;

    @GetMapping("/send")
    public void sendAuthCodeToEmail(
            @RequestParam @Email @NotEmpty String email
    ) {
        memberJoinService.sendAuthCodeToEmail(email);
    }

    @GetMapping("/valid")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyAuthCode(
            @RequestParam @Email @NotEmpty String email,
            @RequestParam @NotEmpty String authCode
    ) {
        if (memberJoinService.verifyAuthCode(email, authCode)) {
            return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(true)));
        }
        return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(false)));
    }

    @GetMapping("/check")
    public ResponseEntity<RestResponse<DuplicateNicknameResponse>> checkDuplicateNickname(
            @RequestParam @NotEmpty String nickname
    ) {
        if (memberJoinService.checkDuplicateNickname(nickname)) {
            return ResponseEntity.ok(RestResponse.success(DuplicateNicknameResponse.of(true)));
        }
        return ResponseEntity.ok(RestResponse.success(DuplicateNicknameResponse.of(false)));
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinMember(
            @Valid @RequestBody MemberJoinRequest memberJoinRequest
    ) {
        memberJoinService.joinMember(memberJoinRequest.toDto());
        return ResponseEntity.status(CREATED).build();
    }
}
