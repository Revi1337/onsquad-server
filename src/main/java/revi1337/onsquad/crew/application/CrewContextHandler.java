package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.event.CrewContextDisposed;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.squad.application.SquadContextHandler;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewContextHandler {

    private final SquadContextHandler squadContextHandler;
    private final CrewRepository crewRepository;
    private final CrewContextDisposer crewContextDisposer;
    private final ApplicationEventPublisher eventPublisher;

    public List<Crew> findMyCrews(Long memberId) {
        return crewRepository.findAllByMemberId(memberId);
    }

    public void disposeContext(Crew crew) {
        squadContextHandler.disposeContexts(squadContextHandler.findSquadIdsByCrewIdIn(List.of(crew.getId())));
        crewContextDisposer.disposeContext(crew.getId());
        String crewImage = crew.hasImage() ? crew.getImageUrl() : null;

        eventPublisher.publishEvent(CrewContextDisposed.of(crew.getId(), crewImage));
    }

    public void removeMemberFromCrews(Long memberId, List<Crew> ownedCrews, List<AnnounceReference> announceReferences) {
        List<Long> ownedCrewIds = Crew.extractIds(ownedCrews);
        List<String> crewImagesToDelete = Crew.extractImageUrls(ownedCrews);
        crewContextDisposer.disposeMemberActivity(memberId, ownedCrewIds);

        eventPublisher.publishEvent(CrewContextDisposed.of(ownedCrewIds, crewImagesToDelete, announceReferences));
    }
}
