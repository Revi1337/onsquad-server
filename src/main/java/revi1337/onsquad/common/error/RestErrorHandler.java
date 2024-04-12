package revi1337.onsquad.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.CommonErrorCode;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;

import java.util.*;

import static org.springframework.http.HttpStatus.*;

@RestController
public class RestErrorHandler implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<RestResponse<ProblemDetail>> handleError(HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = new RequestDispatcherResolver(httpServletRequest).resolveHttpStatus();
        return switch (httpStatus) {
            case BAD_REQUEST -> ResponseEntity.status(BAD_REQUEST)
                    .body(RestResponse.fail(ProblemDetail.of(CommonErrorCode.INVALID_INPUT_VALUE)));

            case NOT_FOUND -> ResponseEntity.status(NOT_FOUND)
                    .body(RestResponse.fail(ProblemDetail.of(CommonErrorCode.NOT_FOUND)));

            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(RestResponse.fail(ProblemDetail.of(CommonErrorCode.INTERNAL_SERVER_ERROR)));
        };
    }

    @RequestMapping("/errors")
    public RestResponse<Map<String, String>> handleErrors() {
        ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider =
                new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter typeFilter = new AssignableTypeFilter(ErrorCode.class);
        classPathScanningCandidateComponentProvider.addIncludeFilter(typeFilter);

        return RestResponse.success(
                buildCommonErrorResponse(classPathScanningCandidateComponentProvider)
        );
    }

    private Map<String, String> buildCommonErrorResponse(ClassPathScanningCandidateComponentProvider provider) {
        Set<BeanDefinition> components = provider.findCandidateComponents("revi1337.onsquad");
        Map<String, String> errors = new HashMap<>();
        for (BeanDefinition component : components) {
            try {
                Class<?> className = Class.forName(component.getBeanClassName());
                if (className.isEnum()) {
                    for (var errorCode : className.getEnumConstants()) {
                        String code = (String) errorCode.getClass().getMethod("getCode").invoke(errorCode);
                        String description = (String) errorCode.getClass().getMethod("getDescription").invoke(errorCode);
                        errors.put(code, description);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("unexpected error occurred");
            }
        }
        return errors;
    }
}
