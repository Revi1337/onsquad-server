package revi1337.onsquad.squad_request.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestRejectedContext;

class RequestRejectNotificationTest {

    @Test
    void constructor() {
        RequestRejectedContext context = new RequestRejectedContext(
                1L,
                "테니스 크루",
                10L,
                "주말 복식 매치",
                1L,
                2L,
                "태영"
        );

        RequestRejectNotification notification = new RequestRejectNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.rejecterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.requesterId());
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.SQUAD_REJECT);

            RequestRejectNotification.RequestRejectPayload payload = notification.getPayload();
            softly.assertThat(payload.crewId()).isEqualTo(context.crewId());
            softly.assertThat(payload.crewName()).isEqualTo(context.crewName());
            softly.assertThat(payload.squadId()).isEqualTo(context.squadId());
            softly.assertThat(payload.squadTitle()).isEqualTo(context.squadTitle());

            softly.assertThat(payload.message())
                    .as("스쿼드 거절 메시지가 올바르게 생성되어야 한다.")
                    .isEqualTo("스쿼드 합류가 거절되었습니다.");
        });
    }
}
