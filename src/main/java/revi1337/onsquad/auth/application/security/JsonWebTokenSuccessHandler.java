package revi1337.onsquad.auth.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import revi1337.onsquad.auth.application.JsonWebTokenProvider;
import revi1337.onsquad.auth.application.JsonWebTokenService;
import revi1337.onsquad.auth.application.RefreshTokenStoreService;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;

import java.io.IOException;
import java.util.Collections;

import static org.springframework.http.MediaType.*;

@RequiredArgsConstructor
@Slf4j
public class JsonWebTokenSuccessHandler implements AuthenticationSuccessHandler {

    private final JsonWebTokenService jsonWebTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("{} --> onAuthenticationSuccess", getClass().getSimpleName());
        if (authentication.getPrincipal() instanceof AuthenticatedMember authenticatedMember) {
            JsonWebTokenResponse jsonWebTokenResponse = jsonWebTokenService.generateTokenPairResponse(Collections.singletonMap("identifier", authenticatedMember.id()));
            jsonWebTokenService.storeTemporaryTokenInMemory(jsonWebTokenResponse.refreshToken(), authenticatedMember.toDto());
            sendTokenResponseToClient(response, jsonWebTokenResponse);
            return;
        }

        throw new RuntimeException("unexpected principal type");
    }

    private void sendTokenResponseToClient(HttpServletResponse response,
                                           JsonWebTokenResponse jsonWebTokenResponse) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter()
                .write(objectMapper.writeValueAsString(jsonWebTokenResponse));
    }
}
