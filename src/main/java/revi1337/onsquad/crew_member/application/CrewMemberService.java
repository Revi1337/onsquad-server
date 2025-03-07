package revi1337.onsquad.crew_member.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew_member.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    @Transactional(readOnly = true)
    public List<EnrolledCrewDto> fetchAllJoinedCrews(Long memberId) {
        return crewMemberRepository.fetchAllJoinedCrewsByMemberId(memberId).stream()
                .map(EnrolledCrewDto::from)
                .toList();
    }

    public List<Top5CrewMemberDto> findTop5CrewMembers(Long memberId, Long crewId) {
        if (!crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT);
        }

        return crewMemberRepository.findTop5CrewMembers(crewId).stream()
                .map(Top5CrewMemberDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CrewMemberDto> findCrewMembers(Long memberId, Long crewId) {
        if (!crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT);
        }

        return crewMemberRepository.findManagedCrewMembersByCrewId(crewId).stream()
                .map(CrewMemberDto::from)
                .toList();
    }
}
