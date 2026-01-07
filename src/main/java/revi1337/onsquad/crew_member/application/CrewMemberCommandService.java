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

    // TODO 비교적 동시성 문제에 안전하지만, 데이터 불일치(Data Inconsistency) 가능. Crew 에 명시된 크루장이 member1 인데, member1 에 해당하는 crewmember 의 권한이 general 일 수 있음.
    public void delegateOwner(Long memberId, Long crewId, Long targetMemberId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureNotSelfTarget(memberId, targetMemberId);
        CrewMemberPolicy.ensureCanDelegateOwner(me);
        CrewMember nextOwner = crewMemberAccessor.getByMemberIdAndCrewId(targetMemberId, crewId);
        crew.delegateOwner(me, nextOwner);
    }

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
        CrewMemberPolicy.ensureNotSelfTarget(memberId, targetMemberId);
        CrewMember targetMember = crewMemberAccessor.getByMemberIdAndCrewId(targetMemberId, crewId);
        CrewMemberPolicy.ensureCanKickOutMember(me, targetMember);
        targetMember.leaveCrew();
        crewMemberRepository.delete(targetMember);
    }
}
