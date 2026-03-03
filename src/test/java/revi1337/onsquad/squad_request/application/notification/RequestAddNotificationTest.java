package revi1337.onsquad.squad_request.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAddedContext;

class RequestAddNotificationTest {

    @Test
    void constructor() {
        RequestAddedContext context = new RequestAddedContext(
                1L,
                "테니스 크루",
                10L,
                "주말 복식 매치",
                50L,
                "리더닉네임",
                100L,
                2L,
                "태영"
        );

        RequestAddNotification notification = new RequestAddNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.requesterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.squadMemberId());
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.SQUAD_REQUEST);

            RequestAddNotification.RequestPayload payload = notification.getPayload();
            softly.assertThat(payload.crewId()).isEqualTo(context.crewId());
            softly.assertThat(payload.squadId()).isEqualTo(context.squadId());
            softly.assertThat(payload.requestId()).isEqualTo(context.requestId());

            softly.assertThat(payload.message())
                    .as("신청자 닉네임이 템플릿에 맞춰 정확히 포맷팅되어야 한다.")
                    .isEqualTo("태영 님이 스쿼드 합류를 요청하였습니다.");
        });
    }
}
