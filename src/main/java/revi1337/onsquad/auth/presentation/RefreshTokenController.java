package revi1337.onsquad.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.JsonWebTokenService;
import revi1337.onsquad.auth.domain.vo.RefreshToken;
import revi1337.onsquad.auth.dto.request.ReissueRequest;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class RefreshTokenController {

    private final JsonWebTokenService jsonWebTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<RestResponse<JsonWebTokenResponse>> reissueToken(
            @Valid @RequestBody ReissueRequest reissueRequest
    ) {
        JsonWebTokenResponse jsonWebTokenResponse = jsonWebTokenService.reissueToken(
                new RefreshToken(reissueRequest.refreshToken())
        );

        return ResponseEntity.ok().body(RestResponse.success(jsonWebTokenResponse));
    }
}
