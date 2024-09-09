package revi1337.onsquad.squad_member.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadWithMemberDomainDto;

import java.util.List;
import java.util.Optional;

public interface SquadMemberRepository {

    SquadMember save(SquadMember squadMember);

    SquadMember saveAndFlush(SquadMember squadMember);

    List<EnrolledSquadDomainDto> findEnrolledSquads(Long memberId);

    Optional<SquadWithMemberDomainDto> findSquadWithMembers(Long memberId, Name crewName, Long squadId);

    default SquadWithMemberDomainDto getSquadWithMembers(Long memberId, Name crewName, Long squadId) {
        return findSquadWithMembers(memberId, crewName, squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOTFOUND));
    }
}
