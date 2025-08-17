package revi1337.onsquad.squad.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.UNSUFFICIENT_AUTHORITY;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_category.domain.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;

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
