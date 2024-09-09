package revi1337.onsquad.crew_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    public List<EnrolledCrewDto> findOwnedCrews(Long memberId) {
        return crewMemberRepository.findOwnedCrews(memberId).stream()
                .map(EnrolledCrewDto::from)
                .toList();
    }

    public List<CrewMemberDto> findCrewMembers(Long memberId, String crewName) {
        if (!crewMemberRepository.existsCrewMemberInCrew(memberId, new Name(crewName))) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
        
        return crewMemberRepository.findOwnCrewMembers(memberId, new Name(crewName)).stream()
                .map(CrewMemberDto::from)
                .toList();
    }
}
