package revi1337.onsquad.history.domain.repository;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

public interface HistoryJpaRepository extends JpaRepository<HistoryEntity, Long> {

    @Transactional(propagation = REQUIRES_NEW)
    HistoryEntity save(HistoryEntity history);

    @Transactional(propagation = REQUIRES_NEW)
    @Modifying
    @Query("delete HistoryEntity h where h.memberId = :memberId")
    void deleteByMemberId(Long memberId);

}
