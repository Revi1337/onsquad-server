package revi1337.onsquad.crew_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.CrewAccessor;
import revi1337.onsquad.crew.application.CrewContextHandler;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewMemberCommandService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewContextHandler crewContextHandler;
    private final CrewMemberRepository crewMemberRepository;

    public void delegateOwner(Long memberId, Long crewId, Long targetMemberId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureNotSelfTargeting(memberId, targetMemberId);
        CrewMemberPolicy.ensureOwnerDelegatable(me);
        CrewMember nextOwner = crewMemberAccessor.getByMemberIdAndCrewId(targetMemberId, crewId);
        Crew crew = me.getCrew();
        crew.delegateOwner(me, nextOwner);
    }

    public void leaveCrew(Long memberId, Long crewId) { // TODO 동시성 문제 해결 필요. (Optimistic VS Pessimistic VS Atomic Update Query)
        Crew crew = crewAccessor.getById(crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        if (CrewPolicy.isLastMemberRemaining(crew)) {
            crewContextHandler.disposeContextWithSquads(crew);
            return;
        }
        CrewPolicy.ensureLeavable(crew, me);
        me.leaveCrew();
        crewMemberRepository.delete(me);
    }

    public void kickOutMember(Long memberId, Long crewId, Long targetMemberId) {
        CrewMember kicker = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureNotSelfTargeting(memberId, targetMemberId);
        Crew ignored = crewAccessor.getById(crewId);
        CrewMember targetMember = crewMemberAccessor.getByMemberIdAndCrewId(targetMemberId, crewId);
        CrewMemberPolicy.ensureKickable(kicker, targetMember);
        targetMember.leaveCrew();
        crewMemberRepository.delete(targetMember);
    }
}
