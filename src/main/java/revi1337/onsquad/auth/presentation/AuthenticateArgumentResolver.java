package revi1337.onsquad.auth.presentation;

import static revi1337.onsquad.auth.error.TokenErrorCode.EMPTY_TOKEN;
import static revi1337.onsquad.auth.error.TokenErrorCode.INVALID_TOKEN_FORMAT;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import revi1337.onsquad.auth.application.CurrentMember;
import revi1337.onsquad.auth.application.token.ClaimsParser;
import revi1337.onsquad.auth.application.token.JsonWebTokenEvaluator;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.common.constant.Sign;

@RequiredArgsConstructor
public class AuthenticateArgumentResolver implements HandlerMethodArgumentResolver {

    private static final int TOKEN_INDEX = 1;
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticate.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (isTokenDoesntRequired(parameter) && isAuthorizationHeaderNull(authorizationHeader)) {
            return null;
        }

        validateAuthorizationHeader(authorizationHeader);
        final String accessToken = authorizationHeader.split(Sign.WHITESPACE)[TOKEN_INDEX];
        final ClaimsParser claimsParser = jsonWebTokenEvaluator.verifyAccessToken(accessToken);

        return CurrentMember.of(claimsParser.parseIdentity(), claimsParser.parseEmail(), claimsParser.parseUserType());
    }

    private boolean isTokenDoesntRequired(MethodParameter parameter) {
        Authenticate annotation = parameter.getParameterAnnotation(Authenticate.class);
        return !annotation.required();
    }

    private void validateAuthorizationHeader(String authorizationHeader) {
        if (isAuthorizationHeaderNull(authorizationHeader)) {
            throw new AuthTokenException.NeedToken(EMPTY_TOKEN);
        }

        if (isAuthorizationHeaderPrefixInvalid(authorizationHeader)) {
            throw new AuthTokenException.InvalidTokenFormat(INVALID_TOKEN_FORMAT);
        }
    }

    private boolean isAuthorizationHeaderNull(String authorizationHeader) {
        return authorizationHeader == null;
    }

    private boolean isAuthorizationHeaderPrefixInvalid(String authorizationHeader) {
        return !authorizationHeader.startsWith(TOKEN_PREFIX);
    }
}
