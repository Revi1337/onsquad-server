package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.dto.CrewDto;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    public List<CrewDto> findOwnedCrews(Long id) {
        return crewRepository.findAllByMemberId(id)
                .stream()
                .map(CrewDto::from)
                .toList();
    }

    public void acceptCrewMember(CrewDto crewDto) {
        memberRepository.findById(crewDto.getMemberDto().getId());
    }
}
