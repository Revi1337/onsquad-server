package revi1337.onsquad.member.presentation;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.member.application.MemberJoinService;

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
}
