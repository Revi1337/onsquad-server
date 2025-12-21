package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommandService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadRepository squadRepository;
    private final SquadCategoryRepository squadCategoryRepository;

    public Long newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        Squad squad = squadRepository.save(Squad.create(dto.toEntityMetadata(), crewMember.getMember(), crewMember.getCrew()));
        squadCategoryRepository.insertBatch(squad.getId(), Category.fromCategoryTypes(dto.categories()));
        return squad.getId();
    }

    public void deleteSquad(Long memberId, Long squadId) {
        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        squadAccessPolicy.ensureDeletable(squad, memberId);
        squadRepository.deleteById(squadId);
    }
}
