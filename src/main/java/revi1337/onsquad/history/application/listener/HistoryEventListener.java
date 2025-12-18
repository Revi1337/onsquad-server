package revi1337.onsquad.history.application.listener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@RequiredArgsConstructor
@Component
public class HistoryEventListener {

    private final HistoryRepository historyRepository;

    @Transactional(propagation = REQUIRES_NEW)
    @EventListener
    public void onHistory(History history) {
        historyRepository.save(history.toEntity());
    }
}
