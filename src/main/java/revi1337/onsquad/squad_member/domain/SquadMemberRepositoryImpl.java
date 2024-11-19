package revi1337.onsquad.squad_member.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadWithMemberDomainDto;

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
    public List<EnrolledSquadDomainDto> findEnrolledSquads(Long memberId) {
        return squadMemberQueryDslRepository.findEnrolledSquads(memberId);
    }

    @Override
    public Optional<SquadWithMemberDomainDto> findSquadWithMembers(Long memberId, Long crewId, Long squadId) {
        return squadMemberQueryDslRepository.findSquadMembers(memberId, crewId, squadId);
    }
}
