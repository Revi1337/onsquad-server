package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;

@Component
@RequiredArgsConstructor
public class CrewMemberAccessor {

    private final CrewMemberRepository crewMemberRepository;

    public CrewMember getByMemberIdAndCrewId(Long memberId, Long crewId) {
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }

    public Page<CrewMember> fetchParticipantsByCrewId(Long crewId, Pageable pageable) {
        return crewMemberRepository.fetchParticipantsByCrewId(crewId, pageable);
    }

    public List<MyParticipantCrewResult> fetchParticipantCrews(Long memberId) {
        return crewMemberRepository.fetchParticipantCrews(memberId);
    }

    public boolean checkAlreadyParticipant(Long memberId, Long crewId) {
        return crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId);
    }

    public void validateMemberInCrew(Long memberId, Long crewId) {
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
    }

    public void validateMemberNotInCrew(Long memberId, Long crewId) {
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent()) {
            throw new CrewMemberBusinessException.AlreadyParticipant(CrewMemberErrorCode.ALREADY_JOIN);
        }
    }
}
