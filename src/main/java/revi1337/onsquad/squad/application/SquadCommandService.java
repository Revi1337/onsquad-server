package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad_category.domain.SquadCategoryJdbcRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommandService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    public Long newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        Crew crew = crewRepository.getById(crewId);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Squad persistSquad = squadRepository.save(Squad.create(dto.toEntityMetadata(), crewMember, crew));
        squadCategoryJdbcRepository.batchInsert(persistSquad.getId(), Category.fromCategoryTypes(dto.categories()));

        return persistSquad.getId();
    }
}
