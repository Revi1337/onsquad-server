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
}
