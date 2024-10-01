package revi1337.onsquad.crew_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

import java.util.List;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;

@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    @Transactional(readOnly = true)
    public List<EnrolledCrewDto> findOwnedCrews(Long memberId) {
        return crewMemberRepository.findOwnedCrews(memberId).stream()
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
