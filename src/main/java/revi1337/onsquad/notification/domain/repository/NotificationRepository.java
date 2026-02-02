package revi1337.onsquad.notification.domain.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByReceiverId(Long receiverId, Pageable pageable);

    List<NotificationEntity> findAllByReceiverIdAndIdAfterOrderByIdAsc(Long receiverId, Long lastEventId);

    @Transactional
    @Modifying
    @Query("update NotificationEntity ne set ne.read = true where ne.receiverId = :receiverId and ne.read = false")
    int markAllAsRead(Long receiverId);

    @Modifying
    @Query("update NotificationEntity ne set ne.read = true where ne.receiverId = :receiverId and ne.id = :id and ne.read = false")
    int markAsRead(Long receiverId, Long id);

    @Transactional
    @Modifying
    @Query("delete NotificationEntity n where n.receiverId = :receiverId")
    int deleteByReceiverId(Long receiverId);

}
