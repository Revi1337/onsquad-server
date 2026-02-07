package revi1337.onsquad.notification.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.NotificationFixture.createUserNotification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.model.NotificationMessages;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

class NotificationMessageRecovererTest extends ApplicationLayerTestSupport {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMessageRecoverer notificationMessageRecoverer;

    @Test
    @DisplayName("사용자 ID와 마지막 이벤트 ID를 기준으로 누락된 알림 메시지 목록을 복구한다")
    void recover() {
        LocalDateTime baseDateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        NotificationEntity notification1 = createUserNotification(2L, 2L, false, NotificationDetail.COMMENT, baseDateTime.plusDays(4));
        NotificationEntity notification2 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, baseDateTime.plusDays(2));
        NotificationEntity notification3 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, baseDateTime.plusDays(3));
        NotificationEntity notification4 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, baseDateTime.plusDays(4));
        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4));
        clearPersistenceContext();

        NotificationMessages recoveredMessage = notificationMessageRecoverer.recover(2L, 2L);

        assertSoftly(softly -> {
            softly.assertThat(recoveredMessage.getMessages()).hasSize(2);
            softly.assertThat(recoveredMessage.getMessages().get(0).id()).isEqualTo(notification3.getId());
            softly.assertThat(recoveredMessage.getMessages().get(1).id()).isEqualTo(notification4.getId());
        });
    }
}
