package revi1337.onsquad.notification.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeartbeatNotificationTest {

    @Test
    @DisplayName("연결 유지용 알림(HeartbeatNotification)은 토픽과 상세 타입이 HEARTBEAT로 올바르게 초기화된다")
    void constructor() {
        HeartbeatNotification notification = new HeartbeatNotification();

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isNull();
            softly.assertThat(notification.getReceiverId()).isNull();
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.HEARTBEAT);
            softly.assertThat(notification.getPayload()).isNull();
            softly.assertThat(notification.isSelfNotification()).isTrue();
            softly.assertThat(notification.shouldSend()).isFalse();
        });
    }
}
