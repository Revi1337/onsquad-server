package revi1337.onsquad.auth.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import revi1337.onsquad.auth.dto.request.LoginRequest;
import revi1337.onsquad.auth.error.AuthErrorCode;
import revi1337.onsquad.auth.error.exception.UnsupportedLoginUrlMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
public class JsonWebTokenLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    private static final String DEFAULT_LOGIN_URL = "/api/v1/auth/login";
    private static final String ALLOW_HTTP_METHOD = "POST";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(DEFAULT_LOGIN_URL, ALLOW_HTTP_METHOD);

    public JsonWebTokenLoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        boolean matches = DEFAULT_ANT_PATH_REQUEST_MATCHER.matches(request);
        if (!matches && !response.isCommitted()) {
            try {
                getFailureHandler().onAuthenticationFailure(request, response,
                        new UnsupportedLoginUrlMethod(
                                AuthErrorCode.UNSUPPORTED_LOGIN_METHOD,
                                new HttpRequestMethodNotSupportedException(
                                        request.getMethod(), Collections.singleton(HttpMethod.POST.name())
                                )
                        )
                );
            } catch (ServletException | IOException ignored) {
            }
        }
        return matches;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException {
        LoginRequest loginRequest = objectMapper.readValue(
                StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8), LoginRequest.class
        );
        UsernamePasswordAuthenticationToken unauthenticatedToken = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.email(), loginRequest.password()
        );

        return getAuthenticationManager().authenticate(unauthenticatedToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("{} --> successfulAuthentication", getClass().getSimpleName());
        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("{} --> unsuccessfulAuthentication", getClass().getSimpleName());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
