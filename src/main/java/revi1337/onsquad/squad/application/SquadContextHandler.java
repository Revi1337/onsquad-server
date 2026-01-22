package revi1337.onsquad.squad.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.squad.domain.event.SquadContextDisposed;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SquadContextHandler {

    private final SquadRepository squadRepository;
    private final SquadContextDisposer squadContextDisposer;
    private final ApplicationEventPublisher eventPublisher;

    public List<Long> findSquadIdsByCrews(List<Crew> crews) {
        return squadRepository.findIdsByCrewIdIn(Crew.extractIds(crews));
    }

    public List<Long> findMySquadIds(Long memberId) {
        return squadRepository.findIdsByMemberId(memberId);
    }

    public void disposeContext(Long squadId) {
        squadContextDisposer.disposeContext(squadId);
        eventPublisher.publishEvent(new SquadContextDisposed(squadId));
    }

    public void disposeContexts(List<Long> squadIds) {
        squadContextDisposer.disposeContexts(squadIds);
        eventPublisher.publishEvent(new SquadContextDisposed(squadIds));
    }
}
