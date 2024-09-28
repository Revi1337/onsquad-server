package revi1337.onsquad.crew_member.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheJpaRepository;
import revi1337.onsquad.crew_member.aspect.RedisCache;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;

import java.util.List;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.*;
import static revi1337.onsquad.crew_member.aspect.RedisCache.CommunityType.CREW;

@RequiredArgsConstructor
@Repository
public class CrewMemberRepositoryImpl implements CrewMemberRepository {

    private final CrewMemberQueryDslRepository crewMemberQueryDslRepository;
    private final CrewMemberJpaRepository crewMemberJpaRepository;
    private final CrewTopCacheJpaRepository crewTopCacheJpaRepository;

    @Override
    public CrewMember save(CrewMember crewMember) {
        return crewMemberJpaRepository.save(crewMember);
    }

    @Override
    public CrewMember saveAndFlush(CrewMember crewMember) {
        return crewMemberJpaRepository.saveAndFlush(crewMember);
    }

    @Override
    public Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewMemberJpaRepository.findByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public Optional<CrewMember> findWithMemberByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewMemberJpaRepository.findWithMemberByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public boolean existsByMemberIdAndCrewName(Long memberId, Name name) {
        return crewMemberJpaRepository.existsByMemberIdAndCrewName(memberId, name);
    }

    @Override
    public boolean existsByMemberIdAndCrewId(Long memberId, Long crewId) {
        return crewMemberJpaRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    @RedisCache(type = CREW, id = "crewId", name = "topN", unit = HOURS)
    @Override
    public List<Top5CrewMemberDomainDto> findTop5CrewMembers(Long crewId) {
        return crewTopCacheJpaRepository.findAllByCrewId(crewId).stream()
                .map(Top5CrewMemberDomainDto::from)
                .toList();
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
    public Optional<CrewMember> findCrewMemberByMemberId(Long memberId) {
        return crewMemberJpaRepository.findCrewMemberByMemberId(memberId);
    }

    @Override
    public Optional<CrewMember> findCrewMemberByCrewIdAndMemberId(Long memberId, Long crewId) {
        return crewMemberJpaRepository.findCrewMemberByCrewIdAndMemberId(memberId, crewId);
    }

    @Override
    public List<EnrolledCrewDomainDto> findOwnedCrews(Long memberId) {
        return crewMemberQueryDslRepository.findOwnedCrews(memberId);
    }

    @Override
    public List<CrewMemberDomainDto> findManagedCrewMembersByCrewId(Long crewId) {
        return crewMemberQueryDslRepository.findCrewMembersByCrewId(crewId);
    }
}
