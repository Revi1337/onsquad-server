package revi1337.onsquad.crew_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record CrewMemberDomainDto(
        SimpleMemberDomainDto memberInfo,
        LocalDateTime participantAt
) {
    @QueryProjection
    public CrewMemberDomainDto(SimpleMemberDomainDto memberInfo, LocalDateTime participantAt) {
        this.memberInfo = memberInfo;
        this.participantAt = participantAt;
    }
}
