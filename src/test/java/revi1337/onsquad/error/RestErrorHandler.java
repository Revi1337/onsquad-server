//package revi1337.onsquad.error;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
//import org.springframework.core.type.filter.AssignableTypeFilter;
//import org.springframework.core.type.filter.TypeFilter;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import revi1337.onsquad.common.dto.RestResponse;
//import revi1337.onsquad.common.error.ErrorCode;
//
//@RestController
//class RestErrorHandler {
//
//    private static final String BASE_PACKAGE = "revi1337.onsquad";
//
//    @GetMapping("/errors")
//    public RestResponse<Map<String, String>> handleErrors() {
//        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
//        TypeFilter typeFilter = new AssignableTypeFilter(ErrorCode.class);
//        provider.addIncludeFilter(typeFilter);
//
//        return RestResponse.success(createErrorResponse(provider));
//    }
//
//    private Map<String, String> createErrorResponse(ClassPathScanningCandidateComponentProvider provider) {
//        Set<BeanDefinition> components = provider.findCandidateComponents(BASE_PACKAGE);
//        List<ErrorCode> errorCodes = new ArrayList<>();
//        components.stream()
//                .map(this::getClassName)
//                .filter(Class::isEnum)
//                .forEach(errorClass -> errorCodes.addAll(List.of((ErrorCode[]) errorClass.getEnumConstants())));
//
//        return errorCodes.stream().collect(Collectors.toMap(ErrorCode::getCode, ErrorCode::getDescription));
//    }
//
//    private Class<?> getClassName(BeanDefinition component) {
//        try {
//            return Class.forName(component.getBeanClassName());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("unexpected error occurred");
//        }
//    }
//}
