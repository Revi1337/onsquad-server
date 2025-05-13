package revi1337.onsquad.squad_member.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;

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
    public Optional<SquadMember> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return squadMemberJpaRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public Optional<SquadMember> findWithSquadBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return squadMemberJpaRepository.findWithSquadBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public List<SquadMemberDomainDto> fetchAllBySquadId(Long squadId) {
        return squadMemberQueryDslRepository.fetchAllBySquadId(squadId);
    }

    @Override
    public List<EnrolledSquadDomainDto> fetchAllJoinedSquadsByMemberId(Long memberId) {
        return squadMemberQueryDslRepository.findEnrolledSquads(memberId);
    }
}
