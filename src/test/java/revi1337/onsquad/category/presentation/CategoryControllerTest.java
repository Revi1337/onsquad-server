package revi1337.onsquad.category.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.category.application.CategoryService;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.common.PresentationLayerTestSupport;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest extends PresentationLayerTestSupport {

    @MockBean(name = "cachedCategoryService")
    private CategoryService cachedCategoryService;

    @Nested
    @DisplayName("Category 조회를 문서화한다.")
    class FindCategories {

        @Test
        @DisplayName("Category 조회에 성공한다.")
        void success() throws Exception {
            when(cachedCategoryService.findCategories()).thenReturn(CategoryType.texts());

            mockMvc.perform(get("/api/categories")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("category/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseBody()
                    ));
        }
    }
}
