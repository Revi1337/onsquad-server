package revi1337.onsquad.crew_member.domain;

import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;

public interface CrewMemberRepository {

    CrewMember save(CrewMember crewMember);

    CrewMember saveAndFlush(CrewMember crewMember);

    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    Optional<CrewMember> findWithMemberByCrewIdAndMemberId(Long crewId, Long memberId);

    boolean existsByMemberIdAndCrewName(Long memberId, Name name);

    boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

    List<Top5CrewMemberDomainDto> findTop5CrewMembers(Long crewId);

    boolean existsCrewMember(Long memberId);

    boolean existsParticipantCrewMember(Long memberId);

    Optional<CrewMember> findCrewMemberByMemberId(Long memberId);

    Optional<CrewMember> findCrewMemberByCrewIdAndMemberId(Long memberId, Long crewId);

    List<EnrolledCrewDomainDto> findOwnedCrews(Long memberId);

    List<CrewMemberDomainDto> findManagedCrewMembersByCrewId(Long crewId);

    default CrewMember getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT));
    }

    default CrewMember getWithMemberByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findWithMemberByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT));
    }
}
