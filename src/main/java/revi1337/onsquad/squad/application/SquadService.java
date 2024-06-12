package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.dto.SquadDto;

@RequiredArgsConstructor
@Service
public class SquadService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;

    public void createNewSquad(SquadDto squadDto) {
        Long memberId = squadDto.getMemberDto().getId();
        memberRepository.findById(memberId)
                .ifPresent(member -> squadRepository.save(squadDto.toEntity(member)));
    }
}
