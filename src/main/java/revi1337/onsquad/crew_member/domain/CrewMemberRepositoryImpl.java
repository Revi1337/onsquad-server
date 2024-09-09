package revi1337.onsquad.crew_member.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CrewMemberRepositoryImpl implements CrewMemberRepository {

    private final CrewMemberJpaRepository crewMemberJpaRepository;
    private final CrewMemberQueryDslRepository crewMemberQueryDslRepository;

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
    public boolean existsCrewMemberInCrew(Long memberId, Name name) {
        return crewMemberJpaRepository.existsCrewMemberInCrew(memberId, name);
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
    public List<CrewMemberDomainDto> findOwnCrewMembers(Long memberId, Name crewName) {
        return crewMemberQueryDslRepository.findCrewMembersByMemberIdAndCrewName(memberId, crewName);
    }
}
