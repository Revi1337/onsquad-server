package revi1337.onsquad.common.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class RequestDispatcherResolver {

    private final HttpServletRequest httpServletRequest;

    public String resolveRequestUri() {
        return (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    }

    public String resolveErrorMessage() {
        return (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE);
    }

    public HttpStatus resolveHttpStatus() {
        Integer statusCode = (Integer) httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
