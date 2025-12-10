package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public interface SquadMemberRepository {

    SquadMember save(SquadMember squadMember);

    SquadMember saveAndFlush(SquadMember squadMember);

    void flush();

    void delete(SquadMember squadMember);

    int countBySquadId(Long squadId);

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    List<SquadMemberDomainDto> fetchAllBySquadId(Long squadId);

    List<EnrolledSquadDomainDto> fetchAllJoinedSquadsByMemberId(Long memberId);

}
