package revi1337.onsquad.squad_member.domain;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTFOUND;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadWithMemberDomainDto;

public interface SquadMemberRepository {

    SquadMember save(SquadMember squadMember);

    SquadMember saveAndFlush(SquadMember squadMember);

    List<EnrolledSquadDomainDto> findEnrolledSquads(Long memberId);

    Optional<SquadWithMemberDomainDto> findSquadWithMembers(Long memberId, Long crewId, Long squadId);

    default SquadWithMemberDomainDto getSquadWithMembers(Long memberId, Long crewId, Long squadId) {
        return findSquadWithMembers(memberId, crewId, squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND));
    }
}
