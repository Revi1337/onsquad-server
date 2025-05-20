package revi1337.onsquad.squad_member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.squad_member.application.SquadMemberCommandService;
import revi1337.onsquad.squad_member.application.SquadMemberQueryService;

@WebMvcTest(SquadMemberController.class)
class SquadMemberControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private SquadMemberQueryService squadMemberService;

    @MockBean
    private SquadMemberCommandService squadMemberCommandService;

    @Nested
    @DisplayName("SquadMember 의 Squad 탈퇴를 문서화한다.")
    class Leave {

        @Test
        @DisplayName("SquadMember 의 Squad 탈퇴에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            doNothing().when(squadMemberCommandService).leaveSquad(any(), eq(CREW_ID), eq(SQUAD_ID));

            mockMvc.perform(delete("/api/crews/{crewId}/squads/{squadId}/me", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("squad-member/success/leave",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            responseBody()
                    ));
        }
    }
}