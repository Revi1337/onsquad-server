package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.event.CrewContextDisposed;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.squad.application.SquadContextHandler;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewContextHandler {

    private final CrewRepository crewRepository;
    private final AnnounceRepository announceRepository;
    private final SquadContextHandler squadContextHandler;
    private final CrewContextDisposer crewContextDisposer;
    private final ApplicationEventPublisher eventPublisher;

    public List<Crew> findMyCrews(Long memberId) {
        return crewRepository.findAllByMemberId(memberId);
    }

    public List<AnnounceReference> findMyAnnouncesInOtherCrews(Long memberId) {
        return announceRepository.findAnnounceReferencesByMemberId(memberId);
    }

    public void disposeContext(Crew crew) {
        List<String> crewImages = Crew.extractImageUrls(List.of(crew));
        squadContextHandler.disposeContexts(squadContextHandler.findSquadIdsByCrews(List.of(crew)));
        crewContextDisposer.disposeContext(crew.getId());

        eventPublisher.publishEvent(new CrewContextDisposed(crew.getId(), crewImages));
    }

    public void disposeContexts(List<Crew> crews) {
        List<Long> crewIds = Crew.extractIds(crews);
        List<String> crewImages = Crew.extractImageUrls(crews);
        crewContextDisposer.disposeContexts(crewIds);

        eventPublisher.publishEvent(new CrewContextDisposed(crewIds, crewImages));
    }
}
