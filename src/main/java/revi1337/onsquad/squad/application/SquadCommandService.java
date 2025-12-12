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
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad_member.application.SquadMemberAccessPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommandService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadRepository squadRepository;
    private final SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    public void newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        Squad squad = squadRepository.save(Squad.create(dto.toEntityMetadata(), crewMember.getMember(), crewMember.getCrew()));
        squadCategoryJdbcRepository.insertBatch(squad.getId(), Category.fromCategoryTypes(dto.categories()));
    }

    public void deleteSquad(Long memberId, Long crewId, Long squadId) {
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        Squad squad = squadMember.getSquad();
        squadAccessPolicy.ensureMatchCrew(squad, crewId);
        squadAccessPolicy.ensureDeletable(squadMember);
        squadRepository.deleteById(squadId);
    }
}
