package revi1337.onsquad.inrastructure.mail.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.inrastructure.mail.application.AuthMailService;
import revi1337.onsquad.inrastructure.mail.presentation.dto.response.EmailValidResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthMailController {

    private final AuthMailService authMailService;

    @GetMapping("/auth/send")
    public ResponseEntity<RestResponse<String>> sendAuthCodeToEmail(
            @RequestParam @Email @NotEmpty String email
    ) {
        authMailService.sendAuthCodeToEmail(email);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyAuthCode(
            @RequestParam @Email @NotEmpty String email,
            @RequestParam @NotEmpty String authCode
    ) {
        if (authMailService.verifyAuthCode(email, authCode)) {
            return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(false)));
    }
}
