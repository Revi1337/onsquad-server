package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    public List<CrewMemberDto> fetchCrewMembers(Long memberId, Long crewId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return crewMemberRepository.findManagedCrewMembersByCrewId(crewId).stream()
                .map(CrewMemberDto::from)
                .toList();
    }
}
