package revi1337.onsquad.crew_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

import java.util.List;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;

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

    // TODO 캐싱 필요
    public List<Top5CrewMemberDto> findTop5CrewMembers(Long memberId, Long crewId) {
        if (!crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT);
        }

        return crewMemberRepository.findTop5CrewMembers(crewId).stream()
                .map(Top5CrewMemberDto::from)
                .toList();
    }

    public List<CrewMemberDto> findCrewMembers(Long memberId, String crewName) {
        if (!crewMemberRepository.existsByMemberIdAndCrewName(memberId, new Name(crewName))) {
            throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT);
        }
        
        return crewMemberRepository.findOwnCrewMembers(memberId, new Name(crewName)).stream()
                .map(CrewMemberDto::from)
                .toList();
    }
}
