package revi1337.onsquad.common.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;

@RestController
class RestErrorHandlerTest {

    private static final String BASE_PACKAGE = "revi1337.onsquad";

    @GetMapping("/errors")
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
        Set<BeanDefinition> components = provider.findCandidateComponents(BASE_PACKAGE);
        List<ErrorCode> errorCodes = new ArrayList<>();
        for (BeanDefinition component : components) {
            try {
                Class<?> className = Class.forName(component.getBeanClassName());
                if (className.isEnum()) {
                    errorCodes.addAll(List.of((ErrorCode[]) className.getEnumConstants()));
                }
            } catch (Exception e) {
                throw new RuntimeException("unexpected error occurred");
            }
        }

        return errorCodes.stream().collect(
                Collectors.toMap(ErrorCode::getCode, ErrorCode::getDescription)
        );
    }
}

//    private Map<String, String> buildCommonErrorResponse(ClassPathScanningCandidateComponentProvider provider) {
//        Set<BeanDefinition> components = provider.findCandidateComponents(BASE_PACKAGE);
//        Map<String, String> errors = new HashMap<>();
//        for (BeanDefinition component : components) {
//            try {
//                Class<?> className = Class.forName(component.getBeanClassName());
//                if (className.isEnum()) {
//                    Object[] constants = className.getEnumConstants();
//                    for (var errorCode : constants) {
////                        values = (ErrorCode[]) errorCode.getClass().getMethod("values").invoke(errorCode);
//                        String code = (String) errorCode.getClass().getMethod("getCode").invoke(errorCode);
//                        String description = (String) errorCode.getClass().getMethod("getDescription").invoke(errorCode);
//                        errors.put(code, description);
//                    }
//                    ErrorCode[] enumConstants = (ErrorCode[]) constants;
//                    for (ErrorCode enumConstant : enumConstants) {
//                        System.out.println("enumConstant = " + enumConstant.getDescription());
//                    }
//                    System.out.println();
//                }
//            } catch (Exception e) {
//                throw new RuntimeException("unexpected error occurred");
//            }
//        }
//        return errors;
//    }