package revi1337.onsquad.common.error;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.ResultActions;
import revi1337.onsquad.support.RestDocumentationSupport;

@WebMvcTest(RestErrorHandlerTest.class)
public class RestErrorHandlerDocumentationTest extends RestDocumentationSupport {

    private static final String BASE_PACKAGE = "revi1337.onsquad";

    @Test
    @DisplayName("ErrorCode 문서화")
    public void errorCodeDocumentation() throws Exception {
        // given & when
        ResultActions result = mockMvc.perform(
                get("/errors")
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        document("error-code/errorCode",
                                codeResponseFields(
                                        "code-response",
                                        beneathPath("data"),
                                        attributes(key("title").value("에러 코드")),
                                        enumConvertFieldDescriptor(
                                                retrieveErrorCodeConstants(ErrorCode.class)
                                        )
                                )
                        )
                );
    }

    private static CodeResponseFieldsSnippet codeResponseFields(String type,
                                                                PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                Map<String, Object> attributes,
                                                                FieldDescriptor... descriptors) {
        return new CodeResponseFieldsSnippet(
                type,
                Arrays.asList(descriptors),
                attributes,
                true,
                subsectionExtractor
        );
    }

    private FieldDescriptor[] enumConvertFieldDescriptor(List<ErrorCode> errorCodes) {
        return errorCodes.stream()
                .map(enumType -> fieldWithPath(enumType.getCode())
                        .description(enumType.getDescription())
                        .type(enumType.getStatus()))
                .toArray(FieldDescriptor[]::new);
    }

    private List<ErrorCode> retrieveErrorCodeConstants(Class<?> clazz) {
        ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider =
                new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter typeFilter = new AssignableTypeFilter(clazz);
        classPathScanningCandidateComponentProvider.addIncludeFilter(typeFilter);

        return buildCommonErrorResponse(classPathScanningCandidateComponentProvider);
    }

    private List<ErrorCode> buildCommonErrorResponse(ClassPathScanningCandidateComponentProvider provider) {
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
        return errorCodes;
    }
}
