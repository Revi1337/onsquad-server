package revi1337.onsquad.crew_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@RequiredArgsConstructor
@Component
public class CrewMemberAccessPolicy {

    private final CrewMemberRepository crewMemberRepository;

    public CrewMember ensureMemberInCrewAndGet(Long memberId, Long crewId) {
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }

    public boolean alreadyParticipant(Long memberId, Long crewId) {
        return crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    public boolean cannotReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isNotOwner();
    }

    public void ensureMemberInCrew(Long memberId, Long crewId) {
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
    }

    public void ensureMemberNotInCrew(Long memberId, Long crewId) {
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent()) {
            throw new CrewMemberBusinessException.AlreadyParticipant(CrewMemberErrorCode.ALREADY_JOIN);
        }
    }

    public void ensureReadParticipantsAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }
    }
}
