package revi1337.onsquad.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.JsonWebTokenService;
import revi1337.onsquad.auth.application.dto.JsonWebToken;
import revi1337.onsquad.auth.application.token.RefreshToken;
import revi1337.onsquad.auth.presentation.dto.request.ReissueRequest;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class RefreshTokenController {

    private final JsonWebTokenService jsonWebTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<RestResponse<JsonWebToken>> reissueToken(
            @Valid @RequestBody ReissueRequest reissueRequest
    ) {
        JsonWebToken jsonWebToken = jsonWebTokenService.reissueToken(
                new RefreshToken(reissueRequest.refreshToken())
        );

        return ResponseEntity.ok().body(RestResponse.success(jsonWebToken));
    }
}
