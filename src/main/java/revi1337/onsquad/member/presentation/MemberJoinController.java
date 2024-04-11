package revi1337.onsquad.member.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.dto.response.DuplicateNicknameResponse;
import revi1337.onsquad.member.dto.response.EmailValidResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class MemberJoinController {

    private final MemberJoinService memberJoinService;

    @GetMapping("/send")
    public void sendAuthCodeToEmail(
            @RequestParam @Email String email
    ) {
        memberJoinService.sendAuthCodeToEmail(email);
    }

    // TODO 여기 구현해야함.
    @GetMapping("/valid")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyAuthCode(
            @RequestParam @Email String email,
            @RequestParam String authCode
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
}
