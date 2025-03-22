package revi1337.onsquad.auth.application.security;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.application.JsonWebTokenService;
import revi1337.onsquad.auth.application.dto.JsonWebToken;

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
        if (authentication.getPrincipal() instanceof AuthMemberAttribute authMemberAttribute) {
            JsonWebToken jsonWebToken = generateJsonWebTokenPair(authMemberAttribute);
            jsonWebTokenService.storeTemporaryTokenInMemory(jsonWebToken.refreshToken(), authMemberAttribute.id());
            sendTokenResponseToClient(response, jsonWebToken);
            return;
        }

        throw new RuntimeException("unexpected principal type");
    }

    private JsonWebToken generateJsonWebTokenPair(AuthMemberAttribute authMemberAttribute) {
        return jsonWebTokenService.generateTokenPair(authMemberAttribute.toDto());
    }

    private void sendTokenResponseToClient(HttpServletResponse response,
                                           JsonWebToken jsonWebToken) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter()
                .write(objectMapper.writeValueAsString(jsonWebToken));
    }
}
