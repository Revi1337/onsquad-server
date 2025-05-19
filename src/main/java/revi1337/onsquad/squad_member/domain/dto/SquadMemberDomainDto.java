package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SquadMemberDomainDto(
        LocalDateTime participantAt,
        SimpleMemberDomainDto member
) {
    @QueryProjection
    public SquadMemberDomainDto(LocalDateTime participantAt, SimpleMemberDomainDto member) {
        this.member = member;
        this.participantAt = participantAt;
    }
}
