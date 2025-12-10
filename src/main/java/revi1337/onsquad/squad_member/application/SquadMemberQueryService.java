package revi1337.onsquad.squad_member.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberQueryService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final SquadMemberRepository squadMemberRepository;

    public List<EnrolledSquadDto> fetchAllJoinedSquads(Long memberId) {
        return squadMemberRepository.fetchAllJoinedSquadsByMemberId(memberId).stream()
                .map(EnrolledSquadDto::from)
                .toList();
    }

    public List<SquadMemberDto> fetchAllBySquadId(Long memberId, Long squadId) {
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isLeader()) {
            return new ArrayList<>(); // TODO 여기 구현해야 함.
        }

        return squadMemberRepository.fetchAllBySquadId(squadId).stream()
                .map(domainDto -> SquadMemberDto.from(memberId, domainDto))
                .toList();
    }

    private SquadMember validateMemberInSquadAndGet(Long memberId, Long squadId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }
}
