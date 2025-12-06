package revi1337.onsquad.token.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.token.application.TokenReissueService;
import revi1337.onsquad.token.domain.model.JsonWebToken;
import revi1337.onsquad.token.domain.model.RefreshToken;

@WebMvcTest(RefreshTokenController.class)
class RefreshTokenControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private TokenReissueService tokenReissueService;

    @Nested
    @DisplayName("토큰 재발급을 문서화한다.")
    class NewSquad {

        @Test
        @DisplayName("토큰 재발급에 성공한다.")
        void success() throws Exception {
            ReissueRequest REISSUE_REQUEST = new ReissueRequest(REFRESH_TOKEN);
            JsonWebToken REISSUE_TOKEN_PAIR = new JsonWebToken(ACCESS_TOKEN, REFRESH_TOKEN);
            when(tokenReissueService.reissue(any(RefreshToken.class))).thenReturn(REISSUE_TOKEN_PAIR);

            mockMvc.perform(post("/api/auth/reissue")
                            .content(objectMapper.writeValueAsString(REISSUE_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("auth/success/reissue",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(fieldWithPath("refreshToken").description("만료되는 RefreshToken")),
                            responseBody()
                    ));
        }
    }
}
