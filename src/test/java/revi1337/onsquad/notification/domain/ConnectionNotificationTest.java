package revi1337.onsquad.notification.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectionNotificationTest {

    @Test
    @DisplayName("최초 연결용 알림(ConnectionNotification)은 토픽과 상세 타입이 CONNECT로 올바르게 초기화된다")
    void constructor() {
        ConnectionNotification notification = new ConnectionNotification();

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isNull();
            softly.assertThat(notification.getReceiverId()).isNull();
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.CONNECT);
            softly.assertThat(notification.getPayload()).isNull();
            softly.assertThat(notification.isSelfNotification()).isTrue();
            softly.assertThat(notification.shouldSend()).isFalse();
        });
    }
}
