package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;

public interface SquadMemberRepository {

    SquadMember saveAndFlush(SquadMember squadMember);

    void flush();

    void delete(SquadMember squadMember);

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    List<SquadMember> fetchParticipantsBySquadId(Long squadId);

    List<MyParticipantSquadResult> fetchParticipantSquads(Long memberId);

}
