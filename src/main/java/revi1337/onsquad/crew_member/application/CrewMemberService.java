package revi1337.onsquad.crew_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.dto.CrewMemberDto;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    public List<CrewMemberDto> findEnrolledCrewMembers(Long memberId) {
        return crewMemberRepository.findEnrolledCrewMembers(memberId)
                .stream()
                .map(CrewMemberDto::from)
                .toList();
    }
}
