package revi1337.onsquad.squad_member.domain.repository;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_IN_SQUAD;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

public interface SquadMemberRepository {

    SquadMember save(SquadMember squadMember);

    SquadMember saveAndFlush(SquadMember squadMember);

    void flush();

    void delete(SquadMember squadMember);

    int countBySquadId(Long squadId);

    Optional<SquadMember> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    Optional<SquadMember> findWithSquadBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId);

    List<SquadMemberDomainDto> fetchAllBySquadId(Long squadId);

    List<EnrolledSquadDomainDto> fetchAllJoinedSquadsByMemberId(Long memberId);

    default SquadMember getBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return findBySquadIdAndCrewMemberId(squadId, crewMemberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotInSquad(NOT_IN_SQUAD));
    }

    default SquadMember getWithSquadBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return findWithSquadBySquadIdAndCrewMemberId(squadId, crewMemberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotInSquad(NOT_IN_SQUAD));

    }
}
