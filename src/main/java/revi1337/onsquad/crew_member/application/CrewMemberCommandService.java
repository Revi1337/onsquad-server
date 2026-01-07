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

@RequiredArgsConstructor
@Transactional
@Service
public class CrewMemberCommandService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewContextHandler crewContextHandler;
    private final CrewMemberRepository crewMemberRepository;

    public void leaveCrew(Long memberId, Long crewId) { // TODO 동시성 문제 해결 필요. (Optimistic VS Pessimistic VS Atomic Update Query)
        Crew crew = crewAccessor.getById(crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        if (CrewPolicy.isLastMemberRemaining(crew)) {
            crewContextHandler.disposeContext(crew);
            return;
        }
        CrewMemberPolicy.ensureCanLeaveCrew(me);
        me.leaveCrew();
        crewMemberRepository.delete(me);
    }

    public void kickOutMember(Long memberId, Long crewId, Long targetMemberId) {
        Crew ignored = crewAccessor.getById(crewId); // TODO 동시성 문제 해결 필요. (과연 Crew 조회가 필요할까? 그냥 Atomic Update Query로 한번에 날리면 될듯?)
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureNotSelfKickOut(memberId, targetMemberId);
        CrewMember targetMember = crewMemberAccessor.getByMemberIdAndCrewId(targetMemberId, crewId);
        CrewMemberPolicy.ensureCanKickOutMember(me, targetMember);
        targetMember.leaveCrew();
        crewMemberRepository.delete(targetMember);
    }
}
