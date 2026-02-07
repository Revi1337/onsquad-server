package revi1337.onsquad.notification.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.NotificationFixture.createUserNotification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.notification.application.response.NotificationResponse;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

class NotificationQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationQueryService notificationQueryService;

    @Test
    @DisplayName("사용자의 알림 목록을 페이징하여 조회한다")
    void fetchNotifications() {
        LocalDateTime baseDateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        NotificationEntity notification1 = createUserNotification(1L, 2L, false, NotificationDetail.COMMENT, baseDateTime.plusDays(4));
        NotificationEntity notification2 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, baseDateTime.plusDays(2));
        NotificationEntity notification3 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, baseDateTime.plusDays(3));
        NotificationEntity notification4 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, baseDateTime.plusDays(4));
        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id"));

        PageResponse<NotificationResponse> response = notificationQueryService.fetchNotifications(2L, pageRequest);

        assertSoftly(softly -> {
            List<NotificationResponse> results = response.results();
            softly.assertThat(results).hasSize(2);
            softly.assertThat(results.get(0).publisherId()).isEqualTo(3L);
            softly.assertThat(results.get(1).publisherId()).isEqualTo(4L);
        });
    }
}
