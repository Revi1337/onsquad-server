package revi1337.onsquad.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import revi1337.onsquad.auth.security.error.UnsupportedLoginUrlMethod;

@Slf4j
public class JsonWebTokenFailureHandler implements AuthenticationFailureHandler {

    private HandlerExceptionResolver handlerExceptionResolver;

    @Qualifier("handlerExceptionResolver")
    @Autowired
    public void setHandlerExceptionResolver(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) {
        if (exception instanceof UnsupportedLoginUrlMethod unsupportedLoginUrlMethod) {
            handlerExceptionResolver.resolveException(request, response, null,
                    (Exception) unsupportedLoginUrlMethod.getCause());
            return;
        }

        handlerExceptionResolver.resolveException(request, response, null, exception);
    }
}
