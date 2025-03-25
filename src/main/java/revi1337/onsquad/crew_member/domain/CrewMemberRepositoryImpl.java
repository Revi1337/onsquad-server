package revi1337.onsquad.crew_member.domain;

import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheJpaRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;

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
    public Boolean existsByMemberIdAndCrewId(Long memberId, Long crewId) {
        return crewMemberJpaRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    @Cacheable(cacheNames = CREW_TOP_USERS, key = "'crew:' + #crewId", cacheManager = "caffeineCacheManager")
    @Override
    public List<Top5CrewMemberDomainDto> findTop5CrewMembers(Long crewId) {
        return crewTopCacheJpaRepository.findAllByCrewId(crewId).stream()
                .map(Top5CrewMemberDomainDto::from)
                .collect(Collectors.toList());
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
    public List<EnrolledCrewDomainDto> fetchAllJoinedCrewsByMemberId(Long memberId) {
        return crewMemberQueryDslRepository.fetchAllJoinedCrewsByMemberId(memberId);
    }

    @Override
    public List<CrewMemberDomainDto> findManagedCrewMembersByCrewId(Long crewId) {
        return crewMemberQueryDslRepository.findCrewMembersByCrewId(crewId);
    }
}
