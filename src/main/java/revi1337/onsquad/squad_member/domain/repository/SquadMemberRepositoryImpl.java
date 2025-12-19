package revi1337.onsquad.squad_member.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.result.EnrolledSquadResult;
import revi1337.onsquad.squad_member.domain.result.SquadMemberResult;

@RequiredArgsConstructor
@Repository
public class SquadMemberRepositoryImpl implements SquadMemberRepository {

    private final SquadMemberJpaRepository squadMemberJpaRepository;
    private final SquadMemberQueryDslRepository squadMemberQueryDslRepository;

    @Override
    public SquadMember save(SquadMember squadMember) {
        return squadMemberJpaRepository.save(squadMember);
    }

    @Override
    public SquadMember saveAndFlush(SquadMember squadMember) {
        return squadMemberJpaRepository.saveAndFlush(squadMember);
    }

    @Override
    public void flush() {
        squadMemberJpaRepository.flush();
    }

    @Override
    public void delete(SquadMember squadMember) {
        squadMemberJpaRepository.delete(squadMember);
    }

    @Override
    public int countBySquadId(Long squadId) {
        return squadMemberJpaRepository.countBySquadId(squadId);
    }

    @Override
    public Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId) {
        return squadMemberJpaRepository.findBySquadIdAndMemberId(squadId, memberId);
    }

    @Override
    public List<SquadMemberResult> fetchAllBySquadId(Long squadId) {
        return squadMemberQueryDslRepository.fetchAllBySquadId(squadId);
    }

    @Override
    public List<EnrolledSquadResult> fetchAllJoinedSquadsByMemberId(Long memberId) {
        return squadMemberQueryDslRepository.findEnrolledSquads(memberId);
    }
}
