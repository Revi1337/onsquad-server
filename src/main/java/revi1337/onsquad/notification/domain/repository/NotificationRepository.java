package revi1337.onsquad.notification.domain.repository;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByReceiverId(Long receiverId, Pageable pageable);

    List<NotificationEntity> findAllByReceiverIdAndIdAfter(Long receiverId, Long lastEventId);

    @Modifying
    @Query("update NotificationEntity ne set ne.read = true where ne.receiverId = :receiverId and ne.read = false")
    int markAllAsRead(Long receiverId);

    @Modifying
    @Query("update NotificationEntity ne set ne.read = true where ne.receiverId = :receiverId and ne.id = :id and ne.read = false")
    int markAsRead(Long receiverId, Long id);

    @Transactional(propagation = REQUIRES_NEW)
    @Modifying
    @Query("delete NotificationEntity n where n.receiverId = :receiverId")
    void deleteByReceiverId(Long receiverId);

}
