package revi1337.onsquad.member.application;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.crew.application.CrewContextHandler;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.event.MemberContextDisposed;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.squad.application.SquadContextHandler;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberContextHandler {

    private final SquadContextHandler squadContextHandler;
    private final CrewContextHandler crewContextHandler;
    private final MemberContextDisposer memberContextDisposer;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void disposeContext(Member member) {
        List<Crew> myCrews = crewContextHandler.findMyCrews(member.getId());
        List<Long> mySquadIds = squadContextHandler.findMySquadIds(member.getId());
        List<Long> squadIdsInCrews = squadContextHandler.findSquadIdsByCrews(myCrews);
        List<Long> squadIdsToRemove = collectSquadIdsToRemove(mySquadIds, squadIdsInCrews);

        squadContextHandler.disposeContexts(squadIdsToRemove);
        memberContextDisposer.disposeMemberActivityFromSquads(member.getId());

        crewContextHandler.disposeContexts(myCrews);
        memberContextDisposer.disposeMemberActivityFromCrews(member.getId());

        List<AnnounceReference> announcesInOtherCrews = crewContextHandler.findMyAnnouncesInOtherCrews(member.getId());
        memberRepository.deleteById(member.getId());
        eventPublisher.publishEvent(new MemberContextDisposed(member.getId(), getMemberImage(member), announcesInOtherCrews));
    }

    private List<Long> collectSquadIdsToRemove(List<Long> mySquadIds, List<Long> squadIdsInCrews) {
        return Stream.concat(mySquadIds.stream(), squadIdsInCrews.stream())
                .distinct()
                .toList();
    }

    private String getMemberImage(Member member) {
        if (member.hasImage()) {
            return member.getProfileImage();
        }
        return null;
    }
}
