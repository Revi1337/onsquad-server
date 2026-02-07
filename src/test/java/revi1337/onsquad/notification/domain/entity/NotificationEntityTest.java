package revi1337.onsquad.notification.domain.entity;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

class NotificationEntityTest {

    @Test
    @DisplayName("notification 객체 생성에 성공한다.")
    void constructor() {
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .receiverId(1L)
                .publisherId(2L)
                .topic(NotificationTopic.USER)
                .detail(NotificationDetail.COMMENT)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .occurredAt(LocalDateTime.now())
                .build();

        assertSoftly(softly -> {
            softly.assertThat(notificationEntity.getReceiverId()).isEqualTo(1L);
            softly.assertThat(notificationEntity.getPublisherId()).isEqualTo(2L);
            softly.assertThat(notificationEntity.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notificationEntity.getDetail()).isSameAs(NotificationDetail.COMMENT);
            softly.assertThat(notificationEntity.isRead()).isFalse();
            softly.assertThat(notificationEntity.getJson()).isEqualTo("{\"message\": \"hello\", \"count\": 1}");
            softly.assertThat(notificationEntity.getOccurredAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("occurredAt 필드를 적지 않아도 내부에서 시간이 측정된다.")
    void constructor2() {
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .receiverId(1L)
                .publisherId(2L)
                .topic(NotificationTopic.USER)
                .detail(NotificationDetail.COMMENT)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .build();

        assertSoftly(softly -> {
            softly.assertThat(notificationEntity.getReceiverId()).isEqualTo(1L);
            softly.assertThat(notificationEntity.getPublisherId()).isEqualTo(2L);
            softly.assertThat(notificationEntity.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notificationEntity.getDetail()).isSameAs(NotificationDetail.COMMENT);
            softly.assertThat(notificationEntity.isRead()).isFalse();
            softly.assertThat(notificationEntity.getJson()).isEqualTo("{\"message\": \"hello\", \"count\": 1}");
            softly.assertThat(notificationEntity.getOccurredAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("notification 의 receiverId 가 빠지면 NPE 가 발생한다.")
    void constructorFail() {
        assertThatNullPointerException().isThrownBy(() -> NotificationEntity.builder()
                .publisherId(2L)
                .topic(NotificationTopic.USER)
                .detail(NotificationDetail.COMMENT)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .occurredAt(LocalDateTime.now())
                .build()
        ).withMessage("receiverId cannot be null");
    }

    @Test
    @DisplayName("notification 의 publisherId 가 빠지면 NPE 가 발생한다.")
    void constructorFail2() {
        assertThatNullPointerException().isThrownBy(() -> NotificationEntity.builder()
                .receiverId(1L)
                .topic(NotificationTopic.USER)
                .detail(NotificationDetail.COMMENT)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .occurredAt(LocalDateTime.now())
                .build()
        ).withMessage("publisherId cannot be null");
    }

    @Test
    @DisplayName("notification 의 topic 이 빠지면 NPE 가 발생한다.")
    void constructorFail3() {
        assertThatNullPointerException().isThrownBy(() -> NotificationEntity.builder()
                .receiverId(1L)
                .publisherId(2L)
                .detail(NotificationDetail.COMMENT)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .occurredAt(LocalDateTime.now())
                .build()
        ).withMessage("topic cannot be null");
    }

    @Test
    @DisplayName("notification 의 detail 이 빠지면 NPE 가 발생한다.")
    void constructorFail4() {
        assertThatNullPointerException().isThrownBy(() -> NotificationEntity.builder()
                .receiverId(1L)
                .publisherId(2L)
                .topic(NotificationTopic.USER)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .occurredAt(LocalDateTime.now())
                .build()
        ).withMessage("detail cannot be null");
    }
}
