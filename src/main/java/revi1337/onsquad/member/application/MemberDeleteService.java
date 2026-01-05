package revi1337.onsquad.member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.crew.application.CrewContextHandler;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.event.MemberDeleted;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.squad.application.SquadContextHandler;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberDeleteService {

    private final MemberAccessor memberAccessor;
    private final MemberRepository memberRepository;
    private final AnnounceRepository announceRepository;
    private final SquadContextHandler squadContextHandler;
    private final CrewContextHandler crewContextHandler;
    private final ApplicationEventPublisher eventPublisher;

    public void deleteMember(Long memberId) {
        Member member = memberAccessor.getById(memberId);
        List<Crew> ownedCrews = crewContextHandler.findMyCrews(memberId);
        List<Long> squadIdsToRemove = squadContextHandler.findOwnedSquadIds(memberId, Crew.extractIds(ownedCrews));
        List<AnnounceReference> announceReferences = announceRepository.findAnnounceReferencesByMemberId(memberId);

        squadContextHandler.removeMemberFromSquads(memberId, squadIdsToRemove);
        crewContextHandler.removeMemberFromCrews(memberId, ownedCrews, announceReferences);
        memberRepository.deleteById(memberId);
        eventPublisher.publishEvent(new MemberDeleted(memberId, getMemberImage(member)));
    }

    private String getMemberImage(Member member) {
        if (member.hasImage()) {
            return member.getProfileImage();
        }
        return null;
    }
}
