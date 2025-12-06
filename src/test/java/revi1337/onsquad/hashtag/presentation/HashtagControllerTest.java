package revi1337.onsquad.hashtag.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.hashtag.application.HashtagService;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

@WebMvcTest(HashtagController.class)
class HashtagControllerTest extends PresentationLayerTestSupport {

    @MockBean(name = "cachedHashtagService")
    private HashtagService hashtagService;

    @Nested
    @DisplayName("모든 카테고리 조회를 테스트한다.")
    class GetAllCategories {

        @Test
        @DisplayName("모든 카테고리 조회에 성공한다.")
        void success() throws Exception {
            List<String> hashtags = HashtagType.unmodifiableList()
                    .stream().map(HashtagType::getText)
                    .toList();
            when(hashtagService.findHashtags()).thenReturn(hashtags);

            mockMvc.perform(get("/api/hashtags")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("hashtag/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseBody()
                    ));
        }
    }
}
