package revi1337.onsquad.crew_member.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewMemberRepositoryImpl implements CrewMemberRepository {

    private final CrewMemberQueryDslRepository crewMemberQueryDslRepository;
    private final CrewMemberJpaRepository crewMemberJpaRepository;

    @Override
    public CrewMember save(CrewMember crewMember) {
        return crewMemberJpaRepository.save(crewMember);
    }

    @Override
    public CrewMember saveAndFlush(CrewMember crewMember) {
        return crewMemberJpaRepository.saveAndFlush(crewMember);
    }

    @Override
    public void deleteAllByCrewId(Long crewId) {
        crewMemberJpaRepository.deleteAllByCrewId(crewId);
    }

    @Override
    public Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewMemberJpaRepository.findByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public Boolean existsByMemberIdAndCrewId(Long memberId, Long crewId) {
        return crewMemberJpaRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    @Override
    public boolean existsCrewMember(Long memberId) {
        return crewMemberJpaRepository.existsCrewMember(memberId);
    }

    @Override
    public boolean existsParticipantCrewMember(Long memberId) {
        return crewMemberJpaRepository.existsParticipantCrewMember(memberId);
    }

    @Override
    public List<EnrolledCrewDomainDto> fetchEnrolledCrewsByMemberId(Long memberId) {
        return crewMemberQueryDslRepository.fetchEnrolledCrewsByMemberId(memberId);
    }

    @Override
    public Page<CrewMemberDomainDto> findManagedCrewMembersByCrewId(Long crewId, Pageable pageable) {
        return crewMemberQueryDslRepository.fetchAllByCrewId(crewId, pageable);
    }
}
