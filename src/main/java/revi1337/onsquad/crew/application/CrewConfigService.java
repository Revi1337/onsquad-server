package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.dto.CrewDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return crewRepository.findOwnedCrews(memberId)
                .stream()
                .toList();
    }

    public void acceptCrewMember(CrewDto crewDto) {
        memberRepository.findById(crewDto.getMemberDto().getId());
    }
}
