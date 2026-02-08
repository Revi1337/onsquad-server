package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;

public interface SquadMemberRepository {

    SquadMember saveAndFlush(SquadMember squadMember);

    void flush();

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    List<SquadMember> fetchParticipantsBySquadId(Long squadId);

    List<MyParticipantSquad> fetchParticipantSquads(Long memberId);

    void delete(SquadMember squadMember);

    int deleteByMemberId(Long memberId);

    int deleteBySquadIdIn(List<Long> squadIds);

}
