package revi1337.onsquad.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_USER_TYPE_VALUE;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.member.application.MemberCommandService;
import revi1337.onsquad.member.application.MemberQueryService;
import revi1337.onsquad.member.application.dto.MemberInfoDto;
import revi1337.onsquad.member.presentation.dto.request.MemberCreateRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberPasswordUpdateRequest;
import revi1337.onsquad.member.presentation.dto.request.MemberUpdateRequest;

@WebMvcTest({MemberController.class})
public class MemberControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private MemberQueryService memberQueryService;

    @MockBean
    private MemberCommandService memberCommandService;

    @Nested
    @DisplayName("닉네임 중복 검사를 문서화한다.")
    class CheckDuplicateNickname {

        @Test
        @DisplayName("사용자 닉네임 중복 검사에 성공한다.")
        void success() throws Exception {
            when(memberQueryService.checkDuplicateNickname(REVI_NICKNAME_VALUE)).thenReturn(true);

            mockMvc.perform(get("/api/members/check-nickname")
                            .param("value", REVI_NICKNAME_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.duplicate").value(true))
                    .andDo(document("member/success/check-nickname",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("value").description("검사할 Nickname")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("이메일 중복 검사를 문서화한다.")
    class CheckDuplicateEmail {

        @Test
        @DisplayName("사용자 이메일 중복 검사에 성공한다.")
        void success() throws Exception {
            when(memberQueryService.checkDuplicateEmail(REVI_EMAIL_VALUE)).thenReturn(true);

            mockMvc.perform(get("/api/members/check-email")
                            .param("value", REVI_EMAIL_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.duplicate").value(true))
                    .andDo(document("member/success/check-email",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("value").description("검사할 Email")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 생성을 문서화한다.")
    class NewMember {

        @Test
        @DisplayName("사용자 생성에 성공한다.")
        void success() throws Exception {
            MemberCreateRequest JOIN_REQUEST = new MemberCreateRequest(
                    REVI_EMAIL_VALUE,
                    REVI_PASSWORD_VALUE,
                    REVI_PASSWORD_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_ADDRESS_VALUE,
                    REVI_ADDRESS_DETAIL_VALUE
            );
            doNothing().when(memberCommandService).newMember(any());

            mockMvc.perform(post("/api/members")
                            .content(objectMapper.writeValueAsString(JOIN_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("member/success/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("email").description("이메일"),
                                    fieldWithPath("password").description("비밀번호"),
                                    fieldWithPath("passwordConfirm").description("비밀번호 확인"),
                                    fieldWithPath("nickname").description("닉네임"),
                                    fieldWithPath("address").description("주소"),
                                    fieldWithPath("addressDetail").description("상세 주소")
                            ),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("비밀번호와 비밀번호 확인란이 일치하지 않으면, 사용자 생성에 실패한다.")
        void fail() throws Exception {
            MemberCreateRequest JOIN_REQUEST = new MemberCreateRequest(
                    REVI_EMAIL_VALUE,
                    REVI_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_ADDRESS_VALUE,
                    REVI_ADDRESS_DETAIL_VALUE
            );

            mockMvc.perform(post("/api/members")
                            .content(objectMapper.writeValueAsString(JOIN_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andDo(document("member/fail/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("내 정보를 문서화한다.")
    class FindMember {

        @Test
        @DisplayName("내 정보 조회에 성공한다.")
        void success() throws Exception {
            MemberInfoDto MEMBER_INFO_DTO = new MemberInfoDto(
                    1L,
                    REVI_EMAIL_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_INTRODUCE_VALUE,
                    REVI_MBTI_VALUE,
                    REVI_KAKAO_LINK,
                    REVI_PROFILE_IMAGE_LINK,
                    REVI_USER_TYPE_VALUE,
                    REVI_ADDRESS_VALUE,
                    REVI_ADDRESS_DETAIL_VALUE
            );
            when(memberQueryService.findMember(any())).thenReturn(MEMBER_INFO_DTO);

            mockMvc.perform(get("/api/members/me")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("member/success/me",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 업데이트를 문서화한다.")
    class UpdateMember {

        @Test
        @DisplayName("사용자 업데이트를 성공한다.")
        void success() throws Exception {
            MemberUpdateRequest UPDATE_REQUEST = new MemberUpdateRequest(
                    REVI_NICKNAME_VALUE,
                    REVI_INTRODUCE_VALUE,
                    REVI_MBTI_VALUE,
                    REVI_KAKAO_LINK,
                    REVI_ADDRESS_VALUE,
                    REVI_ADDRESS_DETAIL_VALUE
            );
            doNothing().when(memberCommandService).updateMember(any(), any());

            mockMvc.perform(put("/api/members/me")
                            .content(objectMapper.writeValueAsString(UPDATE_REQUEST))
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("member/success/update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 비밀번호 변경을 문서화한다.")
    class UpdatePassword {

        @Test
        @DisplayName("사용자 비밀번호 변경을 성공한다.")
        void success() throws Exception {
            MemberPasswordUpdateRequest UPDATE_REQUEST = new MemberPasswordUpdateRequest(
                    REVI_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE
            );
            doNothing().when(memberCommandService).updatePassword(any(), any());

            mockMvc.perform(patch("/api/members/me/password")
                            .content(objectMapper.writeValueAsString(UPDATE_REQUEST))
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("member/success/update-password",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            requestFields(
                                    fieldWithPath("currentPassword").description("기존 비밀번호"),
                                    fieldWithPath("newPassword").description("새로운 비밀번호"),
                                    fieldWithPath("newPasswordConfirm").description("새로운 비밀번호 확인")
                            ),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("새로운 비밀번호와 새로운 비밀번호 확인란이 일치하지 않으면 실패한다.")
        void fail1() throws Exception {
            MemberPasswordUpdateRequest UPDATE_REQUEST = new MemberPasswordUpdateRequest(
                    REVI_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    KWANGWON_PASSWORD_VALUE
            );
            doNothing().when(memberCommandService).updatePassword(any(), any());

            mockMvc.perform(patch("/api/members/me/password")
                            .content(objectMapper.writeValueAsString(UPDATE_REQUEST))
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andDo(document("member/fail/update-password",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 이미지 변경을 문서화한다.")
    class UpdateImage {

        @Test
        @DisplayName("사용자 이미지 변경에 성공한다.")
        void success() throws Exception {
            MockMultipartFile MULTIPART = new MockMultipartFile(
                    "file",
                    "dummy.png",
                    MULTIPART_FORM_DATA_VALUE,
                    "dummy".getBytes(StandardCharsets.UTF_8)
            );
            doNothing().when(memberCommandService).updateImage(any(), any());

            mockMvc.perform(multipart("/api/members/me/image")
                            .file(MULTIPART)
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("member/success/update-image",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            requestParts(partWithName("file").description("사용자 프로필 이미지")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 이미지 삭제를 문서화한다.")
    class DeleteImage {

        @Test
        @DisplayName("사용자 이미지 삭제에 성공한다.")
        void success() throws Exception {
            mockMvc.perform(delete("/api/members/me/image")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("member/success/delete-image",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));
        }
    }
}
