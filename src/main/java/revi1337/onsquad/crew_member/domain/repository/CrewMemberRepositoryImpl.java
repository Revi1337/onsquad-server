package revi1337.onsquad.crew_member.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.CrewMemberWithCountResult;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;

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
    public List<CrewMemberWithCountResult> fetchParticipantsWithCountByCrewId(Long crewId, Pageable pageable) {
        return crewMemberQueryDslRepository.fetchParticipantsWithCountByCrewId(crewId, pageable);
    }

    @Override
    public List<MyParticipantCrewResult> fetchParticipantCrews(Long memberId) {
        return crewMemberQueryDslRepository.fetchParticipantCrews(memberId);
    }
}
