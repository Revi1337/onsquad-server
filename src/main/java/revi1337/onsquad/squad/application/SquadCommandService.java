package revi1337.onsquad.squad.application;

import java.util.List;
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
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@Service
@Transactional
@RequiredArgsConstructor
public class SquadCommandService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadRepository squadRepository;
    private final SquadCategoryRepository squadCategoryRepository;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadContextHandler squadContextHandler;
    private final ApplicationEventPublisher eventPublisher;

    public Long newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        Squad squad = squadRepository.save(Squad.create(dto.toEntityMetadata(), me.getMember(), me.getCrew()));
        List<SquadCategory> squadCategories = createSquadCategories(squad, Category.fromCategoryTypes(dto.categories()));
        squadCategoryRepository.insertBatch(squadCategories);
        eventPublisher.publishEvent(new SquadCreated(crewId, memberId));
        return squad.getId();
    }

    public void deleteSquad(Long memberId, Long squadId) {
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        CrewMember meInCrew = crewMemberAccessor.getByMemberIdAndCrewId(memberId, me.getSquad().getCrew().getId());
        SquadPolicy.ensureDeletable(me, meInCrew);
        squadContextHandler.disposeContext(squadId);
    }

    private List<SquadCategory> createSquadCategories(Squad squad, List<Category> categories) {
        return categories.stream()
                .map(category -> new SquadCategory(squad, category))
                .toList();
    }
}
