package revi1337.onsquad.auth.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.application.AuthService;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.presentation.dto.request.MemberJoinRequest;
import revi1337.onsquad.auth.presentation.dto.response.DuplicateNicknameResponse;
import revi1337.onsquad.auth.presentation.dto.response.EmailValidResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @GetMapping("/send")
    public ResponseEntity<RestResponse<String>> sendAuthCodeToEmail(
            @RequestParam @Email @NotEmpty String email
    ) {
        authService.sendAuthCodeToEmail(email);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/valid")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyAuthCode(
            @RequestParam @Email @NotEmpty String email,
            @RequestParam @NotEmpty String authCode
    ) {
        if (authService.verifyAuthCode(email, authCode)) {
            return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(false)));
    }

    @GetMapping("/check")
    public ResponseEntity<RestResponse<DuplicateNicknameResponse>> checkDuplicateNickname(
            @RequestParam @NotEmpty String nickname
    ) {
        if (authService.checkDuplicateNickname(nickname)) {
            return ResponseEntity.ok(RestResponse.success(DuplicateNicknameResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(DuplicateNicknameResponse.of(false)));
    }

    @PostMapping("/join")
    public ResponseEntity<RestResponse<String>> joinMember(
            @Valid @RequestBody MemberJoinRequest memberJoinRequest
    ) {
        authService.joinMember(memberJoinRequest.toDto());

        return ResponseEntity.ok().body(RestResponse.created());
    }
}
