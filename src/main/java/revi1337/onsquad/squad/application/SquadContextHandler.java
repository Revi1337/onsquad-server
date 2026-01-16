package revi1337.onsquad.squad.application;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.domain.event.SquadContextDisposed;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SquadContextHandler {

    private final SquadRepository squadRepository;
    private final SquadContextDisposer squadContextDisposer;
    private final ApplicationEventPublisher eventPublisher;

    public List<Long> findSquadIdsByCrewIdIn(List<Long> crewIds) {
        return squadRepository.findIdsByCrewIdIn(crewIds);
    }

    public List<Long> findOwnedSquadIds(Long memberId, List<Long> ownedCrewIds) {
        List<Long> myOwnedSquadIds = squadRepository.findIdsByMemberId(memberId);
        List<Long> squadIdsInCrews = squadRepository.findIdsByCrewIdIn(ownedCrewIds);

        return Stream.concat(myOwnedSquadIds.stream(), squadIdsInCrews.stream())
                .distinct()
                .toList();
    }

    public void disposeContext(Long squadId) {
        squadContextDisposer.disposeContext(squadId);
        eventPublisher.publishEvent(new SquadContextDisposed(squadId));
    }

    public void disposeContexts(List<Long> squadIds) {
        squadContextDisposer.disposeContexts(squadIds);
        eventPublisher.publishEvent(new SquadContextDisposed(squadIds));
    }

    public void removeMemberFromSquads(Long memberId, List<Long> squadIdsToRemove) {
        squadContextDisposer.disposeMemberActivity(memberId, squadIdsToRemove);
        eventPublisher.publishEvent(new SquadContextDisposed(squadIdsToRemove));
    }
}
