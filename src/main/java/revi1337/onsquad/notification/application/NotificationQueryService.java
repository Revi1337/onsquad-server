package revi1337.onsquad.notification.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.notification.application.response.NotificationResponse;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final NotificationResponseAssembler assembler;

    public List<NotificationResponse> fetchNotifications(Long memberId, Pageable pageable) {
        return notificationRepository.findAllByReceiverId(memberId, pageable).stream()
                .map(assembler::assemble)
                .toList();
    }
}
