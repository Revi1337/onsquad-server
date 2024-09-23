package revi1337.onsquad.crew_member.domain.dto;

import revi1337.onsquad.member.domain.vo.Nickname;

import java.time.LocalDateTime;

public record Top5CrewMemberDomainDto(
        int rank,
        int counter,
        Long memberId,
        Nickname nickname,
        LocalDateTime participateAt
) {
}
