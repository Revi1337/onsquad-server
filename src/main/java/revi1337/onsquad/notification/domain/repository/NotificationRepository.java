package revi1337.onsquad.notification.domain.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByReceiverId(Long receiverId, Pageable pageable);

    List<NotificationEntity> findAllByReceiverIdAndIdAfter(Long receiverId, Long lastEventId);

    @Modifying
    @Query("update NotificationEntity ne set ne.read = true where ne.receiverId = :receiverId")
    void markAllAsReadByReceiverId(Long receiverId);

}
