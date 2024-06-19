package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewAcceptDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return crewRepository.findOwnedCrews(memberId)
                .stream()
                .toList();
    }

    public List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(String crewName, Long memberId) {
        return crewMemberRepository.findMembersForSpecifiedCrew(new Name(crewName), memberId);
    }

    public void acceptCrewMember(CrewAcceptDto crewAcceptDto, Long memberId) {
        memberRepository.findById(memberId);
    }
}
