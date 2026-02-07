package revi1337.onsquad.notification.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.notification.application.NotificationCommandService;
import revi1337.onsquad.notification.application.NotificationQueryService;
import revi1337.onsquad.notification.application.NotificationService;
import revi1337.onsquad.notification.application.response.NotificationResponse;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.notification.infrastructure.sse.NamedSseEmitter;
import revi1337.onsquad.notification.infrastructure.sse.SseTopic;
import revi1337.onsquad.token.application.ClaimsParser;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationCommandService commandService;

    @MockBean
    private NotificationQueryService queryService;

    @Nested
    @DisplayName("실시간 알림 연결(SSE)을 문서화한다.")
    class connect {

        @Test
        @DisplayName("SSE 연결에 성공한다.")
        void success() throws Exception {
            Long userId = 1L;
            Long lastEventId = 100L;
            ClaimsParser mockClaimsParser = mock(ClaimsParser.class);
            given(jsonWebTokenEvaluator.verifyAccessToken(ACCESS_TOKEN)).willReturn(mockClaimsParser);
            given(mockClaimsParser.parseIdentity()).willReturn(1L);
            NamedSseEmitter emitter = new NamedSseEmitter(userId.toString(), SseTopic.USER, 60 * 60 * 1000L);
            given(notificationService.connect(eq(userId), eq(lastEventId))).willReturn(emitter);

            mockMvc.perform(get("/api/notifications/sse")
                            .param("accessToken", ACCESS_TOKEN)
                            .header("Last-Event-ID", lastEventId)
                            .accept(TEXT_EVENT_STREAM))
                    .andDo(document("notifications/success/sse-connect",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("accessToken").description("사용자 JWT 액세스 토큰 (EventSource 헤더 제약으로 인해 파라미터로 전달)")
                            ),
                            requestHeaders(
                                    headerWithName("Last-Event-ID").description("마지막으로 수신한 이벤트 ID (유실된 알림 복구용)").optional()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 알림 조회를 문서화한다.")
    class fetchNotifications {

        @Test
        @DisplayName("사용자 알림 조회에 성공한다.")
        void success() throws Exception {
            List<NotificationResponse> results = getNotificationResponse();
            Page<NotificationResponse> page = new PageImpl<>(results, PageRequest.of(0, 4), results.size());
            PageResponse<NotificationResponse> pageResponse = PageResponse.from(page);
            given(queryService.fetchNotifications(any(), any(Pageable.class))).willReturn(pageResponse);

            mockMvc.perform(get("/api/notifications")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .param("page", "1")
                            .param("size", "4")
                            .contentType(APPLICATION_JSON))
                    .andDo(document("notifications/success/me",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            queryParameters(
                                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                    parameterWithName("size").description("한 페이지당 개수").optional()
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자 알림을 읽음 처리를 문서화한다.")
    class readNotification {

        @Test
        @DisplayName("사용자 알림 읽기 처리에 성공한다.")
        void success() throws Exception {
            doNothing().when(commandService).read(any(), any());

            mockMvc.perform(patch("/api/notifications/{notificationId}/read", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("notifications/success/read",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("notificationId").description("읽음 처리할 알림 ID")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("사용자의 모든 알림을 읽음 처리를 문서화한다.")
    class readNotifications {

        @Test
        @DisplayName("사용자의 모든 알림 읽기 처리에 성공한다.")
        void success() throws Exception {
            doNothing().when(commandService).read(any(), any());

            mockMvc.perform(patch("/api/notifications/read-all")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("notifications/success/reads",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));
        }
    }

    private List<NotificationResponse> getNotificationResponse() {
        LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        NotificationResponse response1 = new NotificationResponse(
                3L,
                2L,
                3L,
                NotificationTopic.USER,
                NotificationDetail.CREW_REQUEST,
                baseTime.plusDays(6),
                false,
                objectMapper.createObjectNode()
        );
        NotificationResponse response2 = new NotificationResponse(
                2L,
                2L,
                3L,
                NotificationTopic.USER,
                NotificationDetail.CREW_ACCEPT,
                baseTime.plusDays(5),
                false,
                objectMapper.createObjectNode()
        );
        NotificationResponse response3 = new NotificationResponse(
                1L,
                2L,
                3L,
                NotificationTopic.USER,
                NotificationDetail.CREW_REJECT,
                baseTime.plusDays(4),
                false,
                objectMapper.createObjectNode()
        );
        return List.of(response1, response2, response3);
    }
}
