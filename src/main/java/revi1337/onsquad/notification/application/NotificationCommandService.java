package revi1337.onsquad.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;

    public void read(Long memberId, Long notificationId) {
        notificationRepository.markAsRead(memberId, notificationId);
    }

    @Throttling(name = "throttle-notification", key = "'user:' + #memberId")
    public void readAll(Long memberId) {
        notificationRepository.markAllAsRead(memberId);
    }
}
