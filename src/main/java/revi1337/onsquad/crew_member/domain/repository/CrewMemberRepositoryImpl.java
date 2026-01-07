package revi1337.onsquad.crew_member.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;

@RequiredArgsConstructor
@Repository
public class CrewMemberRepositoryImpl implements CrewMemberRepository {

    private final CrewMemberQueryDslRepository crewMemberQueryDslRepository;
    private final CrewMemberJpaRepository crewMemberJpaRepository;

    @Override
    public Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewMemberJpaRepository.findByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public boolean existsByMemberIdAndCrewId(Long memberId, Long crewId) {
        return crewMemberJpaRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    @Override
    public Page<CrewMember> fetchParticipantsByCrewId(Long crewId, Pageable pageable) {
        return crewMemberQueryDslRepository.fetchParticipantsByCrewId(crewId, pageable);
    }

    @Override
    public List<MyParticipantCrewResult> fetchParticipantCrews(Long memberId) {
        return crewMemberQueryDslRepository.fetchParticipantCrews(memberId);
    }

    @Override
    public void delete(CrewMember crewMember) {
        crewMemberJpaRepository.delete(crewMember);
    }

    @Override
    public int deleteByMemberId(Long memberId) {
        return crewMemberJpaRepository.deleteByMemberId(memberId);
    }

    @Override
    public int deleteByCrewIdIn(List<Long> crewIds) {
        return crewMemberJpaRepository.deleteByCrewId(crewIds);
    }
}
