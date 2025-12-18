package revi1337.onsquad.history.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@RequiredArgsConstructor
@Component
public class HistoryEventListener {

    private final HistoryRepository historyRepository;

    @EventListener
    public void onHistory(History history) {
        historyRepository.save(history.toEntity());
    }
}
