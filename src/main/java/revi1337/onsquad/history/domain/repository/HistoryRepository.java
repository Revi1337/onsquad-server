package revi1337.onsquad.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

}
