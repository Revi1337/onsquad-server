package revi1337.onsquad.squad.application.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;
import revi1337.onsquad.squad.domain.event.SquadContextDisposed;
import revi1337.onsquad.squad_category.application.SquadCategoryCacheService;

@Component
@RequiredArgsConstructor
public class SquadContextEventListener {

    private final SquadCategoryCacheService squadCategoryCacheService;

    @TransactionalEventListener
    public void onContextDisposed(SquadContextDisposed contextDisposed) {
        List<Long> squadIds = contextDisposed.squadIds();
        if (CollectionUtils.isEmpty(squadIds)) {
            return;
        }

        squadCategoryCacheService.evictSquadCategories(squadIds);
    }
}
