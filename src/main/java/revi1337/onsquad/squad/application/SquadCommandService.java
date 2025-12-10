package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException.NotParticipant;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommandService {  // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    public void newSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        Squad squad = squadRepository.save(Squad.create(dto.toEntityMetadata(), crewMember.getMember(), crewMember.getCrew()));
        squadCategoryJdbcRepository.batchInsert(squad.getId(), Category.fromCategoryTypes(dto.categories()));
    }

    public void deleteSquad(Long memberId, Long crewId, Long squadId) {
        Squad squad = validateSquadExistsAndGet(squadId);
        if (squad.mismatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (squad.mismatchMemberId(memberId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_MEMBER_REFERENCE);
        }
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isNotLeader()) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
        squadRepository.deleteById(squadId);
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {  // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }

    private Squad validateSquadExistsAndGet(Long squadId) {
        return squadRepository.findById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
    }

    private SquadMember validateMemberInSquadAndGet(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }
}
