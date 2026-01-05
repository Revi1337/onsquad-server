package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;

@RequiredArgsConstructor
@Repository
public class SquadMemberRepositoryImpl implements SquadMemberRepository {

    private final SquadMemberJpaRepository squadMemberJpaRepository;
    private final SquadMemberQueryDslRepository squadMemberQueryDslRepository;

    @Override
    public SquadMember saveAndFlush(SquadMember squadMember) {
        return squadMemberJpaRepository.saveAndFlush(squadMember);
    }

    @Override
    public void flush() {
        squadMemberJpaRepository.flush();
    }

    @Override
    public Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId) {
        return squadMemberJpaRepository.findBySquadIdAndMemberId(squadId, memberId);
    }

    @Override
    public List<SquadMember> fetchParticipantsBySquadId(Long squadId) {
        return squadMemberQueryDslRepository.fetchParticipantsBySquadId(squadId);
    }

    @Override
    public List<MyParticipantSquadResult> fetchParticipantSquads(Long memberId) {
        return squadMemberQueryDslRepository.fetchParticipantSquads(memberId);
    }

    @Override
    public void delete(SquadMember squadMember) {
        squadMemberJpaRepository.delete(squadMember);
    }

    @Override
    public int deleteByMemberId(Long memberId) {
        return squadMemberJpaRepository.deleteByMemberId(memberId);
    }

    @Override
    public int deleteBySquadIdIn(List<Long> squadIds) {
        return squadMemberJpaRepository.deleteBySquadIdIn(squadIds);
    }
}
