package revi1337.onsquad.squad_member.domain;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;
import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_IN_SQUAD;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadInMembersDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMembersWithSquadDomainDto;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException.NotLeader;

public interface SquadMemberRepository {

    SquadMember save(SquadMember squadMember);

    SquadMember saveAndFlush(SquadMember squadMember);

    boolean existsBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    Optional<SquadMember> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    List<SquadMemberDomainDto> fetchAllBySquadId(Long squadId);

    List<EnrolledSquadDomainDto> fetchAllJoinedSquadsByMemberId(Long memberId);

    SquadInMembersDomainDto fetchAllWithCrewAndCategoriesBySquadId(Long crewMemberId, Long squadId);

    Optional<SquadMembersWithSquadDomainDto> fetchMembersWithSquad(Long memberId, Long crewId, Long squadId);

    default SquadMembersWithSquadDomainDto getMembersWithSquad(Long memberId, Long crewId, Long squadId) {
        return fetchMembersWithSquad(memberId, crewId, squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }

    default SquadMember getBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return findBySquadIdAndCrewMemberId(squadId, crewMemberId)
                .orElseThrow(() -> new NotLeader(NOT_IN_SQUAD));
    }
}
