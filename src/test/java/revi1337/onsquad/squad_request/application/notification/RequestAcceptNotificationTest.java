package revi1337.onsquad.squad_request.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAcceptedContext;

class RequestAcceptNotificationTest {

    @Test
    void constructor() {
        RequestAcceptedContext context = new RequestAcceptedContext(
                1L,
                "테니스 크루",
                10L,
                "이번 주말 복식 매치",
                1L,
                2L,
                "신청자닉네임"
        );

        RequestAcceptNotification notification = new RequestAcceptNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.accepterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.requesterId());
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.SQUAD_ACCEPT);

            RequestAcceptNotification.RequestAcceptPayload payload = notification.getPayload();
            softly.assertThat(payload.crewId()).isEqualTo(context.crewId());
            softly.assertThat(payload.crewName()).isEqualTo(context.crewName());
            softly.assertThat(payload.squadId()).isEqualTo(context.squadId());
            softly.assertThat(payload.squadTitle()).isEqualTo(context.squadTitle());

            softly.assertThat(payload.message())
                    .as("스쿼드 수락 메시지가 올바르게 생성되어야 한다.")
                    .isEqualTo("스쿼드에 합류하였습니다. 지금 활동을 시작해보세요!");
        });
    }
}
