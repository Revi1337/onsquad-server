package revi1337.onsquad.squad_member.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadInMembersDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMembersWithSquadDomainDto;

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
    public boolean existsBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return squadMemberJpaRepository.existsBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public Optional<SquadMember> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return squadMemberJpaRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public List<SquadMemberDomainDto> fetchAllBySquadId(Long squadId) {
        return squadMemberQueryDslRepository.fetchAllBySquadId(squadId);
    }

    @Override
    public List<EnrolledSquadDomainDto> fetchAllJoinedSquadsByMemberId(Long memberId) {
        return squadMemberQueryDslRepository.findEnrolledSquads(memberId);
    }

    @Override
    public SquadInMembersDomainDto fetchAllWithCrewAndCategoriesBySquadId(Long crewMemberId, Long squadId) {
//        return squadMemberQueryDslRepository.fetchAllWithCrewAndCategoriesBySquadId2(crewMemberId, squadId); {
        return squadMemberQueryDslRepository.fetchAllWithCrewAndCategoriesBySquadId(crewMemberId, squadId);
    }

    @Override
    public Optional<SquadMembersWithSquadDomainDto> fetchMembersWithSquad(Long memberId, Long crewId, Long squadId) {
        return squadMemberQueryDslRepository.findSquadMembers(memberId, crewId, squadId);
    }
}
