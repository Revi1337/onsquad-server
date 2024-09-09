package revi1337.onsquad.crew_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;

import java.time.LocalDateTime;

public record CrewMemberDomainDto(
        SimpleMemberInfoDomainDto memberInfo,
        LocalDateTime participantAt
) {
    @QueryProjection
    public CrewMemberDomainDto(SimpleMemberInfoDomainDto memberInfo, LocalDateTime participantAt) {
        this.memberInfo = memberInfo;
        this.participantAt = participantAt;
    }
}
