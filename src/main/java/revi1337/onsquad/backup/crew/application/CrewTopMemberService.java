package revi1337.onsquad.backup.crew.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.backup.crew.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.backup.crew.domain.CrewTopMemberCacheRepository;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@RequiredArgsConstructor
@Service
public class CrewTopMemberService {

    private final CrewMemberRepository crewMemberRepository;
    private final CrewTopMemberCacheRepository crewTopMemberCacheRepository;

    public List<Top5CrewMemberDto> findTop5CrewMembers(Long memberId, Long crewId) {
        if (!crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT);
        }

        return crewTopMemberCacheRepository.findAllByCrewId(crewId).stream()
                .map(Top5CrewMemberDto::from)
                .toList();
    }
}
