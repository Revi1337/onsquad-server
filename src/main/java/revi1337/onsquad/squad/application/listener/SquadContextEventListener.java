package revi1337.onsquad.squad.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.squad.domain.event.SquadContextDisposed;
import revi1337.onsquad.squad_category.application.SquadCategoryCacheService;

@Component
@RequiredArgsConstructor
public class SquadContextEventListener {

    private final SquadCategoryCacheService squadCategoryCacheService;

    @TransactionalEventListener
    public void onContextDisposed(SquadContextDisposed contextDisposed) {
        squadCategoryCacheService.evictSquadCategories(contextDisposed.squadIds());
    }
}
