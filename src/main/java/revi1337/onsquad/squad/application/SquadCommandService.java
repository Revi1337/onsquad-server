package revi1337.onsquad.squad.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.UNSUFFICIENT_AUTHORITY;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommandService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    public Long newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Squad persistSquad = squadRepository.save(
                Squad.create(dto.toEntityMetadata(), crewMember, crewMember.getCrew())
        );
        squadCategoryJdbcRepository.batchInsert(persistSquad.getId(), Category.fromCategoryTypes(dto.categories()));

        return persistSquad.getId();
    }

    public void deleteSquad(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadBusinessException.CantDelete(UNSUFFICIENT_AUTHORITY);
        }

        squadRepository.deleteById(squadId);
    }
}
