package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;
import revi1337.onsquad.squad_member.domain.result.SquadMemberResult;

public interface SquadMemberRepository {

    SquadMember saveAndFlush(SquadMember squadMember);

    void flush();

    void delete(SquadMember squadMember);

    int countBySquadId(Long squadId);

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    List<SquadMemberResult> fetchParticipantsBySquadId(Long squadId);

    List<MyParticipantSquadResult> fetchParticipantSquads(Long memberId);

}
