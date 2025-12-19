package revi1337.onsquad.crew_member.domain.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.CrewMemberResult;

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
    public Optional<CrewMember> findWithCrewByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewMemberJpaRepository.findWithCrewByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public Boolean existsByMemberIdAndCrewId(Long memberId, Long crewId) {
        return crewMemberJpaRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    @Override
    public Page<CrewMemberResult> findManagedCrewMembersByCrewId(Long crewId, Pageable pageable) {
        return crewMemberQueryDslRepository.fetchAllByCrewId(crewId, pageable);
    }
}
