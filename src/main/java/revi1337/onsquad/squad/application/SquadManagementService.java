package revi1337.onsquad.squad.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoDto;
import revi1337.onsquad.squad.domain.SquadRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadManagementService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<SimpleSquadInfoDto> findSquadsInCrew(Long memberId, Long crewId) {
        if (!crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }

        return squadRepository.findSquadsInCrew(memberId, crewId).stream()
                .map(SimpleSquadInfoDto::from)
                .toList();
    }
}
