package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.event.SquadCreated;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class SquadCommandService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadRepository squadRepository;
    private final SquadCategoryRepository squadCategoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Long newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        Squad squad = squadRepository.save(Squad.create(dto.toEntityMetadata(), crewMember.getMember(), crewMember.getCrew()));
        squadCategoryRepository.insertBatch(squad.getId(), Category.fromCategoryTypes(dto.categories()));
        eventPublisher.publishEvent(new SquadCreated(crewId, memberId));
        return squad.getId();
    }

    public void deleteSquad(Long memberId, Long squadId) { // TODO 과연.. Hard Delete 가 맞나..? DB 자체 OnDeleteCascade 로 지운다해도.. 연관된 테이블이 Lock 파티가 열릴텐데..? 이거 고민 필요.
        Squad squad = squadAccessor.getById(squadId);
        SquadPolicy.ensureDeletable(squad, memberId);
        squadRepository.deleteById(squadId);
    }
}
