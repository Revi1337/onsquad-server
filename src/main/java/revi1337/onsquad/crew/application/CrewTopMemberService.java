package revi1337.onsquad.crew.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew.application.dto.top.Top5CrewMemberDto;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberCacheRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@RequiredArgsConstructor
@Service
public class CrewTopMemberService {

    private final CrewMemberRepository crewMemberRepository;
    private final CrewTopMemberCacheRepository crewTopMemberCacheRepository;

    public List<Top5CrewMemberDto> findTop5CrewMembers(Long memberId, Long crewId) {
        validateMemberInCrew(memberId, crewId);
        if (!crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT);
        }

        return crewTopMemberCacheRepository.findAllByCrewId(crewId).stream()
                .map(Top5CrewMemberDto::from)
                .toList();
    }

    private void validateMemberInCrew(Long memberId, Long crewId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
    }
}
